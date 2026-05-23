package com.chit_management.chit.service.staff;

import com.chit_management.chit.dto.staff.PasswordDTO;
import com.chit_management.chit.dto.staff.ProfileDTO;
import com.chit_management.chit.dto.staff.UserDTO;

public interface UserAuthService {
    String login(String email, String password);
    UserDTO getMyProfile(String username);
    void updateProfile(String email, ProfileDTO dto);
    void updatePassword(String email, PasswordDTO dto);
}
