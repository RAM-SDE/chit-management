package com.chit_management.chit.respository.chit;

import com.chit_management.chit.entity.chit.ChitEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChitEnrollmentRepository extends JpaRepository<ChitEnrollment, Long> {

    Optional<ChitEnrollment> findByUuid(String uuid);

    // ✅ Check duplicate enrollment
    boolean existsByCustomerIdAndChitPlanId(
            Long customerId, Long chitPlanId);

    // ✅ All enrollments for a plan
    List<ChitEnrollment> findByChitPlanUuid(String planUuid);

    // ✅ All enrollments for a customer
    List<ChitEnrollment> findByCustomerUuid(String customerUuid);

    // ✅ Active enrollments for a customer
    List<ChitEnrollment> findByCustomerUuidAndStatus(
            String customerUuid, ChitEnrollment.Status status);

    // ✅ Server-side list
    @Query("SELECT e FROM ChitEnrollment e " +
            "JOIN e.customer c " +
            "JOIN e.chitPlan p " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
            "LOWER(p.planName) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<ChitEnrollment> findAllEnrollments(
            @Param("search") String search,
            Pageable pageable);
}

