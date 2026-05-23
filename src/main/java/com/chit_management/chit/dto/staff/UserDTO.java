package com.chit_management.chit.dto.staff;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long   id;
    private String uuid;
    private Long   userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private String  roleName;
    private String  gender;

}
