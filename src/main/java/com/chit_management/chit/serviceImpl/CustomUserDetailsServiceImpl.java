package com.chit_management.chit.serviceImpl;

import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.security.CustomUserDetails;
import com.chit_management.chit.respository.staff.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found: " + email);
                });

        // 2. Check active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("Account is disabled: " + email);
        }

        return new CustomUserDetails(user);
    }
}

