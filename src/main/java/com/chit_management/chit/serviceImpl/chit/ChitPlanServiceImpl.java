package com.chit_management.chit.serviceImpl.chit;

import com.chit_management.chit.dto.chit.ChitPlanRequest;
import com.chit_management.chit.dto.chit.ChitPlanResponse;
import com.chit_management.chit.entity.chit.ChitEnrollment;
import com.chit_management.chit.entity.chit.ChitPlan;
import com.chit_management.chit.entity.chit.Payment;
import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.respository.chit.ChitPlanRepository;
import com.chit_management.chit.respository.staff.UserRepository;
import com.chit_management.chit.service.chit.ChitPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChitPlanServiceImpl
        implements ChitPlanService {

    private final ChitPlanRepository chitPlanRepository;
    private final UserRepository     userRepository;

    // ── List with pagination + search ─────────
    @Override
    public Page<ChitPlanResponse> getPlans(String search,
                                           int page,
                                           int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<ChitPlan> result =
                (search != null && !search.isBlank())
                        ? chitPlanRepository.searchByKeyword(search, pageable)
                        : chitPlanRepository.findAllPlans(pageable);

        return result.map(this::toResponse);
    }

    // ── Get by UUID ───────────────────────────
    @Override
    public ChitPlanResponse getPlanByUuid(String uuid) {
        return toResponse(findByUuid(uuid));
    }

    // ── Save ──────────────────────────────────
    @Override
    @Transactional
    public ChitPlanResponse savePlan(ChitPlanRequest dto) {
        ChitPlan plan = new ChitPlan();
        mapDtoToEntity(dto, plan);
        plan.setCreatedBy(getCurrentUser());
        return toResponse(chitPlanRepository.save(plan));
    }

    // ── Update ────────────────────────────────
    @Override
    @Transactional
    public ChitPlanResponse updatePlan(String uuid, ChitPlanRequest dto) {
        ChitPlan existing = findByUuid(uuid);

        // ✅ Cannot edit CANCELLED or COMPLETED plan
        if (existing.getStatus() != ChitPlan.Status.ACTIVE) {
            throw new RuntimeException(
                    "Cannot edit a " + existing.getStatus() + " plan");
        }

        mapDtoToEntity(dto, existing);
        return toResponse(chitPlanRepository.save(existing));
    }

    // ── Update Status ─────────────────────────
    @Override
    @Transactional
    public void updateStatus(String uuid, ChitPlan.Status status) {
        ChitPlan plan = findByUuid(uuid);
        plan.setStatus(status);

        if (status == ChitPlan.Status.CANCELLED) {
            // ✅ CANCELLED — withdraw enrollments AND cancel pending payments
            if (plan.getEnrollments() != null) {
                for (ChitEnrollment enrollment : plan.getEnrollments()) {
                    enrollment.setStatus(ChitEnrollment.Status.WITHDRAWN);
                    if (enrollment.getPayments() != null) {
                        for (Payment payment : enrollment.getPayments()) {
                            if (payment.getStatus() == Payment.Status.PENDING) {
                                payment.setStatus(Payment.Status.CANCELLED);
                            }
                        }
                    }
                }
            }
        }

        chitPlanRepository.save(plan);
    }

    // ── Total active plans ────────────────────
    @Override
    public long getTotalActivePlans() {
        return chitPlanRepository
                .findByStatus(ChitPlan.Status.ACTIVE).size();
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════
    private ChitPlan findByUuid(String uuid) {
        return chitPlanRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException(
                        "Chit plan not found: " + uuid));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private void mapDtoToEntity(ChitPlanRequest dto, ChitPlan entity) {
        // Auto-calculate monthlyAmount and endDate
        BigDecimal monthly = dto.getTotalAmount()
                .divide(BigDecimal.valueOf(dto.getDurationMonths()),
                        2, RoundingMode.HALF_UP);

        entity.setPlanName(dto.getPlanName());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setDurationMonths(dto.getDurationMonths());
        entity.setMonthlyAmount(monthly);
        entity.setTotalMembers(dto.getTotalMembers());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getStartDate()
                .plusMonths(dto.getDurationMonths()));
    }

    private ChitPlanResponse toResponse(ChitPlan c) {
        ChitPlanResponse dto = new ChitPlanResponse();
        dto.setUuid(c.getUuid());
        dto.setPlanName(c.getPlanName());
        dto.setTotalAmount(c.getTotalAmount());
        dto.setDurationMonths(c.getDurationMonths());
        dto.setMonthlyAmount(c.getMonthlyAmount());
        dto.setTotalMembers(c.getTotalMembers());
        dto.setStartDate(c.getStartDate() != null
                ? c.getStartDate().toString() : null);
        dto.setEndDate(c.getEndDate() != null
                ? c.getEndDate().toString() : null);
        dto.setStatus(c.getStatus() != null
                ? c.getStatus().name() : null);
        dto.setCreatedAt(c.getCreatedAt() != null
                ? c.getCreatedAt().toString() : null);
        dto.setCreatedBy(c.getCreatedBy() != null
                ? c.getCreatedBy().getName() : null);
        dto.setActive(c.isActive());
        return dto;
    }

    // ── Soft Delete ───────────────────────────
    @Override
    @Transactional
    public void deactivateChitPlan(String uuid) {
        ChitPlan plan = findByUuid(uuid);
        plan.setActive(false);

        // ✅ Only withdraw enrollments
        // DO NOT touch payments — keep PENDING as PENDING
        if (plan.getEnrollments() != null) {
            for (ChitEnrollment enrollment : plan.getEnrollments()) {
                enrollment.setStatus(ChitEnrollment.Status.WITHDRAWN);
            }
        }

        chitPlanRepository.save(plan);
    }

    @Override
    @Transactional
    public void activateChitPlan(String uuid) {
        ChitPlan plan = findByUuid(uuid);
        plan.setActive(true);

        // ✅ Re-activate enrollments so collection can resume
        if (plan.getEnrollments() != null) {
            for (ChitEnrollment enrollment : plan.getEnrollments()) {
                // Only re-activate WITHDRAWN ones
                // don't touch COMPLETED enrollments
                if (enrollment.getStatus() ==
                        ChitEnrollment.Status.WITHDRAWN) {
                    enrollment.setStatus(ChitEnrollment.Status.ACTIVE);
                }
            }
        }

        chitPlanRepository.save(plan);
    }
}