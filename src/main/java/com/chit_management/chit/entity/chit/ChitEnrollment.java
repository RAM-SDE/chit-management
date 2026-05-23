package com.chit_management.chit.entity.chit;

import com.chit_management.chit.entity.BaseEntity;
import com.chit_management.chit.entity.customer.Customer;
import com.chit_management.chit.entity.staff.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chit_enrollments",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"customer_id", "chit_plan_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChitEnrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chit_plan_id", nullable = false)
    private ChitPlan chitPlan;

    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrolled_by")
    private User enrolledBy;

    @OneToMany(mappedBy = "enrollment",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Payment> payments;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE, COMPLETED, WITHDRAWN
    }
}