package com.chit_management.chit.serviceImpl.chit;

import com.chit_management.chit.dto.chit.PaymentRequest;
import com.chit_management.chit.dto.chit.PaymentResponse;
import com.chit_management.chit.entity.chit.ChitEnrollment;
import com.chit_management.chit.entity.chit.Payment;
import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.respository.chit.ChitEnrollmentRepository;
import com.chit_management.chit.respository.chit.PaymentRepository;
import com.chit_management.chit.respository.staff.UserRepository;
import com.chit_management.chit.security.CustomUserDetails;
import com.chit_management.chit.service.chit.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository        paymentRepository;
    private final ChitEnrollmentRepository enrollmentRepository;
    private final UserRepository           userRepository;

    // ── All payments ──────────────────────────
    @Override
    public Page<PaymentResponse> getAllPayments(
            String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return paymentRepository
                .findAllPayments(search, pageable)
                .map(this::toResponse);
    }

    // ── Pending payments ──────────────────────
    @Override
    public Page<PaymentResponse> getPendingPayments(
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        CustomUserDetails userDetails = getCurrentUserDetails();

        if (userDetails.getPrimaryRole().equals("ROLE_ADMIN")) {
            return paymentRepository
                    .findAllPending(pageable)
                    .map(this::toResponse);
        } else {
            return paymentRepository
                    .findPendingByAgent(
                            userDetails.getUserId(), pageable)
                    .map(this::toResponse);
        }
    }

    // ── Payment history by customer ───────────
    @Override
    public Page<PaymentResponse> getPaymentsByCustomer(
            String customerUuid, int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return paymentRepository
                .findByCustomerUuid(customerUuid, pageable)
                .map(this::toResponse);
    }

    // ── Record payment ────────────────────────
    @Override
    @Transactional
    public List<PaymentResponse> recordPayment(
            PaymentRequest request) {

        ChitEnrollment enrollment = enrollmentRepository
                .findByUuid(request.getEnrollmentUuid())
                .orElseThrow(() -> new RuntimeException(
                        "Enrollment not found"));

        if (enrollment.getStatus() !=
                ChitEnrollment.Status.ACTIVE) {
            throw new RuntimeException(
                    "Enrollment is not active");
        }

        User currentUser  = getCurrentUser();
        String receiptNo  = generateReceiptNo();
        BigDecimal monthly = enrollment.getChitPlan()
                .getMonthlyAmount();

        List<Payment> saved = new ArrayList<>();

        for (Integer monthNumber : request.getMonthNumbers()) {

            // ✅ Check already paid
            if (paymentRepository
                    .existsByEnrollmentIdAndMonthNumber(
                            enrollment.getId(), monthNumber)) {
                throw new RuntimeException(
                        "Month " + monthNumber +
                                " already paid for this enrollment");
            }

            // ✅ Carry forward from previous month
            BigDecimal carryForward = getCarryForward(
                    enrollment.getId(), monthNumber);
            BigDecimal dueAmount = monthly.add(carryForward);

            Payment payment = new Payment();
            payment.setEnrollment(enrollment);
            payment.setMonthNumber(monthNumber);
            payment.setAmountPaid(monthly);
            payment.setDueAmount(dueAmount);
            payment.setCarryForward(carryForward);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMode(request.getPaymentMode());
            payment.setRemarks(request.getRemarks());
            payment.setReceiptNo(receiptNo);
            payment.setCollectedBy(currentUser);

            // ✅ Status based on amount vs due
            if (monthly.compareTo(dueAmount) >= 0) {
                payment.setStatus(Payment.Status.PAID);
            } else {
                payment.setStatus(Payment.Status.PARTIAL);
            }

            saved.add(paymentRepository.save(payment));
        }

        return saved.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Pending months for enrollment ─────────
    @Override
    public List<Integer> getPendingMonths(String enrollmentUuid) {

        ChitEnrollment enrollment = enrollmentRepository
                .findByUuid(enrollmentUuid)
                .orElseThrow(() -> new RuntimeException(
                        "Enrollment not found"));

        int totalMonths = enrollment.getChitPlan()
                .getDurationMonths();

        // ✅ Months already paid
        Set<Integer> paidMonths = paymentRepository
                .findByEnrollmentIdAndStatus(
                        enrollment.getId(), Payment.Status.PAID)
                .stream()
                .map(Payment::getMonthNumber)
                .collect(Collectors.toSet());

        // ✅ Return unpaid months only
        List<Integer> pending = new ArrayList<>();
        for (int i = 1; i <= totalMonths; i++) {
            if (!paidMonths.contains(i)) {
                pending.add(i);
            }
        }
        return pending;
    }

    // ── Dashboard stats ───────────────────────
    @Override
    public BigDecimal getTotalCollected() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails.getPrimaryRole().equals("ROLE_ADMIN")) {
            return paymentRepository.getTotalCollected();
        }
        return paymentRepository.getTotalCollectedByAgent(
                userDetails.getUserId());
    }

    @Override
    public BigDecimal getTotalPending() {
        return paymentRepository.getTotalPending();
    }

    @Override
    public long getPendingCount() {
        return paymentRepository.countByStatus(
                Payment.Status.PENDING);
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════

    // ✅ Calculate carry forward from previous month PARTIAL
    private BigDecimal getCarryForward(Long enrollmentId,
                                       int monthNumber) {
        if (monthNumber <= 1) return BigDecimal.ZERO;

        return paymentRepository
                .findByEnrollmentIdAndStatus(
                        enrollmentId, Payment.Status.PARTIAL)
                .stream()
                .filter(p -> p.getMonthNumber() == monthNumber - 1)
                .findFirst()
                .map(p -> p.getDueAmount()
                        .subtract(p.getAmountPaid()))
                .orElse(BigDecimal.ZERO);
    }

    // ✅ Generate receipt no — reset every year
    private String generateReceiptNo() {
        String year   = String.valueOf(LocalDateTime.now().getYear());
        Pageable top1 = PageRequest.of(0, 1);
        List<String> last = paymentRepository
                .findLastReceiptNo(top1);

        int next = 1;
        if (!last.isEmpty() && last.get(0) != null) {
            String lastNo  = last.get(0);
            String[] parts = lastNo.split("-");
            if (parts.length == 3 && parts[1].equals(year)) {
                try {
                    next = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("RCP-%s-%04d", year, next);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private CustomUserDetails getCurrentUserDetails() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ✅ toResponse — read carryForward from entity directly
    private PaymentResponse toResponse(Payment p) {
        PaymentResponse dto = new PaymentResponse();
        dto.setUuid(p.getUuid());
        dto.setReceiptNo(p.getReceiptNo());
        dto.setCustomerName(p.getEnrollment()
                .getCustomer().getName());
        dto.setCustomerPhone(p.getEnrollment()
                .getCustomer().getPhone());
        dto.setPlanName(p.getEnrollment()
                .getChitPlan().getPlanName());
        dto.setMonthNumber(p.getMonthNumber());
        dto.setAmountPaid(p.getAmountPaid());
        dto.setDueAmount(p.getDueAmount());
        dto.setCarryForward(p.getCarryForward()); // ✅ direct read
        dto.setPaymentMode(p.getPaymentMode().name());
        dto.setStatus(p.getStatus().name());
        dto.setRemarks(p.getRemarks());
        dto.setCollectedBy(p.getCollectedBy() != null
                ? p.getCollectedBy().getName() : null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        dto.setPaymentDate(p.getPaymentDate() != null
                ? p.getPaymentDate().format(formatter) : "—");
        dto.setCreatedAt(p.getCreatedAt() != null
                ? p.getCreatedAt().toString() : null);
        return dto;
    }
}