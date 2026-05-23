package com.chit_management.chit.controller.chitplan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @GetMapping
    public String listPage() {
        return "payment/view-payment";
    }

    @GetMapping("/record")
    public String recordPage() {
        return "payment/record-payment";
    }

    @GetMapping("/pending")
    public String pendingPage() {
        return "payment/pending-payment";
    }

    @GetMapping("/history/{customerUuid}")
    public String historyPage() {
        return "payment/history-payment";
    }
}
