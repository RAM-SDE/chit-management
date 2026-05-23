package com.chit_management.chit.dto.chit;

import com.chit_management.chit.entity.chit.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentRequest {

    @NotBlank(message = "Enrollment is required")
    private String enrollmentUuid;

    @NotEmpty(message = "Select at least one month")
    private List<Integer> monthNumbers;  // ✅ bulk payment

    @NotNull(message = "Payment mode is required")
    private Payment.PaymentMode paymentMode;

    private String remarks;
}
