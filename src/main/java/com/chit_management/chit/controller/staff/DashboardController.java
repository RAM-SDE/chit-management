package com.chit_management.chit.controller.staff;

import com.chit_management.chit.dto.chit.PaymentResponse;
import com.chit_management.chit.dto.staff.PasswordDTO;
import com.chit_management.chit.dto.staff.ProfileDTO;
import com.chit_management.chit.respository.chit.ChitPlanRepository;
import com.chit_management.chit.respository.customer.CustomerRepository;
import com.chit_management.chit.service.staff.UserAuthService;
import com.chit_management.chit.serviceImpl.chit.PaymentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final ChitPlanRepository chitPlanRepository;
    private final PaymentServiceImpl paymentService;
    private final UserAuthService userAuthService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, @RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        model.addAttribute("totalCustomers", customerRepository.findByActiveTrue().size());
        model.addAttribute("totalPlans", chitPlanRepository.count());
        model.addAttribute("activePlans", chitPlanRepository.findByStatus(
                com.chit_management.chit.entity.chit.ChitPlan.Status.ACTIVE).size());
        model.addAttribute("totalCollected", paymentService.getTotalCollected());
        model.addAttribute("totalPending", paymentService.getTotalPending());
        model.addAttribute("pendingCount", paymentService.getPendingCount());
        List<PaymentResponse> recentPayments = paymentService
                .getAllPayments("", 0, 10)
                .getContent()
                .stream().limit(10).toList();

        System.out.println(recentPayments);  // debug

        model.addAttribute("recentPayments", recentPayments);
        return "dashboard";
    }

    // ── Get my profile data ───────────────────────
    @GetMapping("/api/profile")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(
                    userAuthService.getMyProfile(
                            userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Update profile ────────────────────────────
    @PutMapping("/api/profile/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            result.getFieldError().getDefaultMessage()));
        }
        try {
            userAuthService.updateProfile(
                    userDetails.getUsername(), dto);
            return ResponseEntity.ok(
                    Map.of("message",
                            "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Update password ───────────────────────────
    @PutMapping("/api/profile/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            result.getFieldError().getDefaultMessage()));
        }
        try {
            userAuthService.updatePassword(
                    userDetails.getUsername(), dto);
            return ResponseEntity.ok(
                    Map.of("message",
                            "Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
