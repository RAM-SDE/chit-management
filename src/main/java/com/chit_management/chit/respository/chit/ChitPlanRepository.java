package com.chit_management.chit.respository.chit;

import com.chit_management.chit.entity.chit.ChitPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChitPlanRepository extends JpaRepository<ChitPlan, Long> {
    Optional<ChitPlan> findByUuid(String uuid);

    List<ChitPlan> findByStatus(ChitPlan.Status status);

    // Server-side pagination + search
    @Query("SELECT c FROM ChitPlan c WHERE " +
            "LOWER(c.planName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<ChitPlan> searchByKeyword(@Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT c FROM ChitPlan c")
    Page<ChitPlan> findAllPlans(Pageable pageable);
}
