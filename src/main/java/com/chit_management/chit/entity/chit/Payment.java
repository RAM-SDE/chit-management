package com.chit_management.chit.entity.chit;

import com.chit_management.chit.entity.BaseEntity;
import com.chit_management.chit.entity.staff.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private ChitEnrollment enrollment;

    @Column(name = "month_number", nullable = false)
    private Integer monthNumber;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "due_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal dueAmount;

    @Column(name = "carry_forward", precision = 12, scale = 2)
    private BigDecimal carryForward = BigDecimal.ZERO;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING; // ✅ default PENDING not PAID

    @Column(name = "receipt_no", length = 50)
    private String receiptNo;


    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentDate == null) paymentDate = LocalDateTime.now();
    }

    public enum PaymentMode {
        CASH, UPI, BANK_TRANSFER, CHEQUE
    }

    public enum Status {
        PAID, PENDING, PARTIAL, CANCELLED
    }
}
