package com.chit_management.chit.dto.staff;

import com.chit_management.chit.validation.EmptyField;
import com.chit_management.chit.validation.FormatCheck;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupSequence({StaffLogin.class, EmptyField.class, FormatCheck.class})
public class StaffLogin {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(groups = EmptyField.class,message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{6,}$",
            message = "Password must contain at least 1 uppercase letter, 1 number, and 1 special character",
            groups = FormatCheck.class
    )
    private String password;
}
