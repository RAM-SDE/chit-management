package com.chit_management.chit.util;


import com.chit_management.chit.entity.chit.ChitEnrollment;
import com.chit_management.chit.entity.chit.ChitPlan;
import com.chit_management.chit.entity.chit.Payment;
import com.chit_management.chit.respository.chit.ChitEnrollmentRepository;
import com.chit_management.chit.respository.chit.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final ChitEnrollmentRepository enrollmentRepository;
    private final PaymentRepository    paymentRepository;

    // ✅ Runs every day at midnight
    // Checks if today matches any plan's payment day
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateMonthlyPayments() {
        log.info("Running payment scheduler...");

        int today = LocalDate.now().getDayOfMonth();

        // Get all active enrollments
        List<ChitEnrollment> enrollments =
                enrollmentRepository.findAll().stream()
                        .filter(e -> e.getStatus() ==
                                ChitEnrollment.Status.ACTIVE)
                        .filter(e -> e.getChitPlan().isActive())
                        .filter(e -> e.getChitPlan().getStatus() ==
                                ChitPlan.Status.ACTIVE)
                        .toList();

        for (ChitEnrollment enrollment : enrollments) {
            ChitPlan plan = enrollment.getChitPlan();

            // ✅ Check if today matches plan start day
            int planDay = plan.getStartDate().getDayOfMonth();
            if (today != planDay) continue;

            // ✅ Calculate which month number this is
            LocalDate startDate = plan.getStartDate();
            LocalDate today2    = LocalDate.now();
            int monthNumber     = (int) (
                    (today2.getYear() - startDate.getYear()) * 12
                            + today2.getMonthValue()
                            - startDate.getMonthValue() + 1);

            // ✅ Don't exceed plan duration
            if (monthNumber > plan.getDurationMonths()) continue;

            // ✅ Don't create duplicate
            if (paymentRepository
                    .existsByEnrollmentIdAndMonthNumber(
                            enrollment.getId(), monthNumber)) {
                continue;
            }

            // ✅ Create pending payment
            Payment payment = new Payment();
            payment.setEnrollment(enrollment);
            payment.setMonthNumber(monthNumber);
            payment.setAmountPaid(BigDecimal.ZERO);
            payment.setDueAmount(plan.getMonthlyAmount());
            payment.setCarryForward(BigDecimal.ZERO);
            payment.setStatus(Payment.Status.PENDING);
            payment.setPaymentDate(LocalDateTime.now());

            paymentRepository.save(payment);
            log.info("Generated payment for enrollment {} month {}",
                    enrollment.getUuid(), monthNumber);
        }
    }
}
