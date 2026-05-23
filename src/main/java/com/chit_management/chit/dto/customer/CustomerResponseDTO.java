package com.chit_management.chit.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDTO {

    private String uuid;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String aadharNo;
    private boolean active;
    private String createdAt;
}
