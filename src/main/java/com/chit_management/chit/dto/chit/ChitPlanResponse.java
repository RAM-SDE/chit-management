package com.chit_management.chit.dto.chit;

import com.chit_management.chit.entity.chit.ChitPlan;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChitPlanResponse {

    private String      uuid;
    private String      planName;
    private BigDecimal  totalAmount;
    private Integer     durationMonths;
    private BigDecimal  monthlyAmount;
    private Integer     totalMembers;
    private String      startDate;
    private String      endDate;
    private String      status;
    private String      createdAt;
    private String      createdBy;
    private boolean      active;
}
