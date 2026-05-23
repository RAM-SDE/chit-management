package com.chit_management.chit.dto.chit;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChitEnrollmentRequest {

    @NotBlank(message = "Customer is required")
    private String customerUuid;

    @NotBlank(message = "Chit plan is required")
    private String chitPlanUuid;
}
