package com.chit_management.chit.dto.chit;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ✅ Validation order — one by one
@GroupSequence({
        ChitPlanRequest.class,
        ChitPlanRequest.Step1.class,
        ChitPlanRequest.Step2.class,
        ChitPlanRequest.Step3.class,
        ChitPlanRequest.Step4.class,
        ChitPlanRequest.Step5.class
})
public class ChitPlanRequest {

    // ── Validation groups — controls order ────
    public interface Step1 {}
    public interface Step2 {}
    public interface Step3 {}
    public interface Step4 {}
    public interface Step5 {}

    @NotBlank(message = "Plan name is required",
            groups = Step1.class)
    @Size(min = 2, max = 100,
            message = "Plan name must be between 2 and 100 characters",
            groups = Step1.class)
    private String planName;

    @NotNull(message = "Total amount is required",
            groups = Step2.class)
    @DecimalMin(value = "1000.00",
            message = "Total amount must be at least ₹1000",
            groups = Step2.class)
    @Digits(integer = 10, fraction = 2,
            message = "Total amount format is invalid",
            groups = Step2.class)
    private BigDecimal totalAmount;

    @NotNull(message = "Duration is required",
            groups = Step3.class)
    @Min(value = 1,
            message = "Duration must be at least 1 month",
            groups = Step3.class)
    @Max(value = 120,
            message = "Duration cannot exceed 120 months",
            groups = Step3.class)
    private Integer durationMonths;

    @NotNull(message = "Total members is required",
            groups = Step4.class)
    @Min(value = 2,
            message = "Total members must be at least 2",
            groups = Step4.class)
    @Max(value = 500,
            message = "Total members cannot exceed 500",
            groups = Step4.class)
    private Integer totalMembers;

    @NotNull(message = "Start date is required",
            groups = Step5.class)
    @FutureOrPresent(message = "Start date must be today or a future date",
            groups = Step5.class)
    private LocalDate startDate;
}