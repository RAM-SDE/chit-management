package com.chit_management.chit.serviceImpl.chit;

import com.chit_management.chit.dto.chit.ChitEnrollmentRequest;
import com.chit_management.chit.dto.chit.ChitEnrollmentResponse;
import com.chit_management.chit.entity.chit.ChitEnrollment;
import com.chit_management.chit.entity.chit.ChitPlan;
import com.chit_management.chit.entity.customer.Customer;
import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.respository.chit.ChitPlanRepository;
import com.chit_management.chit.respository.customer.CustomerRepository;
import com.chit_management.chit.respository.staff.UserRepository;
import com.chit_management.chit.service.chit.ChitEnrollmentService;
import com.chit_management.chit.respository.chit.ChitEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class chitEnrollmentServiceImpl implements ChitEnrollmentService {

    private final ChitEnrollmentRepository enrollmentRepository;
    private final CustomerRepository customerRepository;
    private final ChitPlanRepository chitPlanRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ChitEnrollmentResponse> getAllEnrollments(
            String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("enrolledAt").descending());
        return enrollmentRepository
                .findAllEnrollments(search, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public ChitEnrollmentResponse enroll(ChitEnrollmentRequest request) {

        Customer customer = customerRepository
                .findByUuid(request.getCustomerUuid())
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found"));

        ChitPlan plan = chitPlanRepository
                .findByUuid(request.getChitPlanUuid())
                .orElseThrow(() -> new RuntimeException(
                        "Chit plan not found"));

        // ✅ Check plan is active
        if (!plan.isActive() ||
                plan.getStatus() != ChitPlan.Status.ACTIVE) {
            throw new RuntimeException(
                    "Cannot enroll in an inactive or cancelled plan");
        }

        // ✅ Check duplicate
        if (enrollmentRepository.existsByCustomerIdAndChitPlanId(
                customer.getId(), plan.getId())) {
            throw new RuntimeException(
                    "Customer already enrolled in this plan");
        }

        ChitEnrollment enrollment = new ChitEnrollment();
        enrollment.setCustomer(customer);
        enrollment.setChitPlan(plan);
        enrollment.setStatus(ChitEnrollment.Status.ACTIVE);
        enrollment.setEnrolledBy(getCurrentUser());

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public void withdraw(String uuid) {
        ChitEnrollment enrollment = findByUuid(uuid);
        enrollment.setStatus(ChitEnrollment.Status.WITHDRAWN);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<ChitEnrollmentResponse> getEnrollmentsByCustomer(
            String customerUuid) {
        return enrollmentRepository
                .findByCustomerUuidAndStatus(
                        customerUuid, ChitEnrollment.Status.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════
    private ChitEnrollment findByUuid(String uuid) {
        return enrollmentRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException(
                        "Enrollment not found: " + uuid));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private ChitEnrollmentResponse toResponse(ChitEnrollment e) {
        ChitEnrollmentResponse dto = new ChitEnrollmentResponse();
        dto.setUuid(e.getUuid());
        dto.setCustomerName(e.getCustomer().getName());
        dto.setCustomerPhone(e.getCustomer().getPhone());
        dto.setPlanName(e.getChitPlan().getPlanName());
        dto.setMonthlyAmount(e.getChitPlan()
                .getMonthlyAmount().toPlainString());
        dto.setEnrolledAt(e.getEnrolledAt() != null
                ? e.getEnrolledAt().toString() : null);
        dto.setStatus(e.getStatus().name());
        dto.setEnrolledBy(e.getEnrolledBy() != null
                ? e.getEnrolledBy().getName() : null);
        return dto;
    }
}
