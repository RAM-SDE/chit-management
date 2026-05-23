package com.chit_management.chit.respository.chit;

import com.chit_management.chit.entity.chit.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByUuid(String uuid);

    // ✅ All payments for an enrollment
    List<Payment> findByEnrollmentId(Long enrollmentId);

    // ✅ Pending payments for an enrollment
    List<Payment> findByEnrollmentIdAndStatus(
            Long enrollmentId, Payment.Status status);

    // ✅ Check if month already paid
    boolean existsByEnrollmentIdAndMonthNumber(
            Long enrollmentId, Integer monthNumber);

    // ✅ Last receipt number for auto-generation
    @Query("SELECT p.receiptNo FROM Payment p " +
            "WHERE p.receiptNo IS NOT NULL " +
            "ORDER BY p.createdAt DESC")
    List<String> findLastReceiptNo(Pageable pageable);

    // ✅ Total collected
    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) " +
            "FROM Payment p WHERE p.status IN ('PAID', 'PARTIAL')")
    BigDecimal getTotalCollected();

    // ✅ Total pending amount
    @Query("SELECT COALESCE(SUM(p.dueAmount), 0) " +
            "FROM Payment p WHERE p.status = 'PENDING'")
    BigDecimal getTotalPending();

    // ✅ Pending count
    long countByStatus(Payment.Status status);

    // ✅ All payments — admin
    @Query("SELECT p FROM Payment p " +
            "JOIN p.enrollment e " +
            "JOIN e.customer c " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
            "c.phone LIKE CONCAT('%',:search,'%') OR " +
            "p.receiptNo LIKE CONCAT('%',:search,'%'))")
    Page<Payment> findAllPayments(@Param("search") String search,
                                  Pageable pageable);

    // ✅ Pending payments — admin sees all
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING'")
    Page<Payment> findAllPending(Pageable pageable);

    // ✅ Pending payments — agent sees only their collections
    @Query("SELECT p FROM Payment p " +
            "JOIN p.enrollment e " +
            "WHERE p.status = 'PENDING' " +
            "AND e.enrolledBy.id = :agentId")
    Page<Payment> findPendingByAgent(@Param("agentId") Long agentId,
                                     Pageable pageable);

    // ✅ Payment history for a customer
    @Query("SELECT p FROM Payment p " +
            "JOIN p.enrollment e " +
            "WHERE e.customer.uuid = :customerUuid " +
            "ORDER BY p.createdAt DESC")
    Page<Payment> findByCustomerUuid(@Param("customerUuid") String customerUuid,
                                     Pageable pageable);

    // ✅ Agent total collected
    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) " +
            "FROM Payment p " +
            "WHERE p.collectedBy.id = :agentId " +
            "AND p.status IN ('PAID', 'PARTIAL')")
    BigDecimal getTotalCollectedByAgent(@Param("agentId") Long agentId);
}