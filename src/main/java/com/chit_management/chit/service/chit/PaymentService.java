package com.chit_management.chit.service.chit;

import com.chit_management.chit.dto.chit.PaymentRequest;
import com.chit_management.chit.dto.chit.PaymentResponse;
import com.chit_management.chit.entity.chit.Payment;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    Page<PaymentResponse> getAllPayments(String search,
                                         int page, int size);
    Page<PaymentResponse> getPendingPayments(int page, int size);
    Page<PaymentResponse> getPaymentsByCustomer(
            String customerUuid, int page, int size);

    List<PaymentResponse> recordPayment(PaymentRequest request);
    List<Integer> getPendingMonths(String enrollmentUuid);

    BigDecimal getTotalCollected();
    BigDecimal getTotalPending();
    long getPendingCount();
}
