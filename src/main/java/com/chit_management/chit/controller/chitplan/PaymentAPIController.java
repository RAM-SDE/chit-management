package com.chit_management.chit.controller.chitplan;

import com.chit_management.chit.dto.chit.PaymentRequest;
import com.chit_management.chit.service.chit.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentAPIController {

    private final PaymentService paymentService;

    // ✅ All payments
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "")  String search,
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "10") int limit) {

        int page = limit > 0 ? offset / limit : 0;
        var result = paymentService
                .getAllPayments(search, page, limit);
        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    // ✅ Pending payments
    @GetMapping("/pending")
    public ResponseEntity<?> pending(
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "10") int limit) {

        int page = limit > 0 ? offset / limit : 0;
        var result = paymentService
                .getPendingPayments(page, limit);
        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    // ✅ Payment history by customer
    @GetMapping("/history/{customerUuid}")
    public ResponseEntity<?> history(
            @PathVariable String customerUuid,
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "10") int limit) {

        int page = limit > 0 ? offset / limit : 0;
        var result = paymentService
                .getPaymentsByCustomer(customerUuid, page, limit);
        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    // ✅ Pending months for enrollment
    @GetMapping("/pending-months/{enrollmentUuid}")
    public ResponseEntity<?> pendingMonths(
            @PathVariable String enrollmentUuid) {
        return ResponseEntity.ok(Map.of(
                "months", paymentService
                        .getPendingMonths(enrollmentUuid)
        ));
    }

    // ✅ Record payment
    @PostMapping
    public ResponseEntity<?> record(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Payment recorded successfully",
                "data",    paymentService.recordPayment(request)
        ));
    }
}
