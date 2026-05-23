package com.chit_management.chit.dto.customer;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CustomerDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Aadhar number must not exceed 20 characters")
    private String aadharNo;
}
