package com.chit_management.chit.entity.chit;

import com.chit_management.chit.entity.BaseEntity;
import com.chit_management.chit.entity.staff.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chit_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class ChitPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "monthly_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyAmount;

    @Column(name = "total_members", nullable = false)
    private Integer totalMembers;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Builder.Default
    @Column(nullable = false , columnDefinition = "TINYINT(1)")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "chitPlan", cascade = CascadeType.ALL)
    private List<ChitEnrollment> enrollments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (!this.active) this.active = true;   // ✅ ensure default on persist
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE, COMPLETED, CANCELLED
    }
}
