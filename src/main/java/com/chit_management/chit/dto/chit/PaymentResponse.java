package com.chit_management.chit.dto.chit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponse {
    private String uuid;
    private String receiptNo;
    private String customerName;
    private String customerPhone;
    private String planName;
    private Integer monthNumber;
    private BigDecimal amountPaid;
    private BigDecimal dueAmount;
    private BigDecimal carryForward;
    private String paymentMode;
    private String status;
    private String remarks;
    private String collectedBy;
    private String paymentDate;
    private String createdAt;
}
