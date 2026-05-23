package com.chit_management.chit.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController {

    // ── Error forwarded from Spring/Security ──
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              Model model) {

        // ✅ Re-throw AccessDeniedException
        // → caught by GlobalExceptionHandler
        Object ex = request.getAttribute(
                "jakarta.servlet.error.exception");
        if (ex instanceof AccessDeniedException ade) {
            throw ade;
        }

        Object status = request.getAttribute(
                RequestDispatcher.ERROR_STATUS_CODE);

        int code = 500;
        if (status != null) {
            code = Integer.parseInt(status.toString());
        }

        switch (code) {
            case 404 -> {
                model.addAttribute("code", "404");
                model.addAttribute("message",
                        "Page not found. The page you are looking for does not exist.");
            }
            case 403 -> {
                model.addAttribute("code", "403");
                model.addAttribute("message",
                        "Access Denied. You do not have permission.");
            }
            case 401 -> {
                model.addAttribute("code", "401");
                model.addAttribute("message",
                        "Unauthorized. Please login first.");
            }
            default -> {
                model.addAttribute("code", String.valueOf(code));
                model.addAttribute("message",
                        "Something went wrong. Please try again.");
            }
        }

        return "error-page";
    }

    // ── Session expired page ───────────────────
    // ✅ No session needed — just return the page
    @GetMapping("/session-expired")
    public String sessionExpiredPage() {
        return "session-expired";
    }

    // ── Error page direct access ───────────────
    // ✅ No session needed — just return the page
    @GetMapping("/error-page")
    public String errorPage() {
        return "error-page";
    }
}