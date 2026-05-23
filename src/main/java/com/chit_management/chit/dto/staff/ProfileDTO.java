package com.chit_management.chit.dto.staff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
public class ProfileDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must be 2-100 characters")
    @Pattern(regexp = "^[A-Za-z\\s.'-]+$",
            message = "Name contains invalid characters")
    private String name;

    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$",
            message = "Invalid phone number")
    private String phone;

    @Size(max = 500, message = "Address too long")
    private String address;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$",
            message = "Gender must be MALE, FEMALE or OTHER")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
}
