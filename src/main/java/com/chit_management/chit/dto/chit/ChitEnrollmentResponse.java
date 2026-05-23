package com.chit_management.chit.dto.chit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChitEnrollmentResponse {

    private String uuid;
    private String customerName;
    private String customerPhone;
    private String planName;
    private String monthlyAmount;
    private String enrolledAt;
    private String status;
    private String enrolledBy;
}
