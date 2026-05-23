package com.chit_management.chit.serviceImpl.staff;

import com.chit_management.chit.dto.staff.PasswordDTO;
import com.chit_management.chit.dto.staff.ProfileDTO;
import com.chit_management.chit.dto.staff.UserDTO;
import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.jwt.JwtUtil;
import com.chit_management.chit.respository.staff.UserRepository;
import com.chit_management.chit.service.staff.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtUtil.generateToken(email);
    }

    @Override
    public UserDTO getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserDTO dto = new UserDTO();
        dto.setUuid(user.getUuid());

        // Load staff profile if exists
        userRepository.findByUuid(user.getUuid())
                .ifPresent(staff -> {
                    dto.setId(staff.getId());
                    dto.setName(staff.getName());
                    dto.setPhone(staff.getPhone());
                    dto.setAddress(staff.getAddress());
                    dto.setGender(staff.getGender() != null
                            ? staff.getGender().name() : null);
                    dto.setIsActive(staff.getIsActive());

                    // Roles
                    if (user.getRoles() != null) {
                        dto.setRoleName(
                                user.getRoles().stream()
                                        .map(r -> r.getRoleName())
                                        .collect(Collectors.joining(", ")));
                    }
                });

        return dto;
    }

    @Override
    public void updateProfile(String email, ProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User staff = userRepository.findByUuid(user.getUuid())
                .orElseThrow(() ->
                        new RuntimeException("User profile not found"));

        // Update only allowed fields
        staff.setName(dto.getName());
        staff.setPhone(dto.getPhone());
        staff.setAddress(dto.getAddress());
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            staff.setGender(User.Gender.valueOf(dto.getGender()));
        }

        userRepository.save(staff);
    }

    @Override
    public void updatePassword(String email, PasswordDTO dto) {

        // 1. Validate confirm password matches
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException(
                    "New password and confirm password do not match");
        }

        // 2. Load user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Verify current password
        if (!passwordEncoder.matches(
                dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 4. Update password
        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
