package com.chit_management.chit.controller.staff;

import com.chit_management.chit.dto.staff.StaffLogin;
import com.chit_management.chit.service.staff.UserAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserAuthService authService;

    @GetMapping(value = {"/login", "/login/"})
    public String loginPage() {
        return "login";
    }

    @Value("${jwt.name}")
    private String NAME;

    @Value("${jwt.expiration}")
    private String EXPIRED ;

    @PostMapping("api/auth/login")
    public ResponseEntity<?> login(@Validated @RequestBody StaffLogin request,
                                   HttpServletResponse response) {

        System.out.println("STEP 1 Controller");
        String token = authService.login(request.getEmail(), request.getPassword());

        Cookie cookie = new Cookie(NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.parseInt(EXPIRED));

        response.addCookie(cookie);
        System.out.println("STEP 5 Response");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Login successful",
                "redirect","/"
        ));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        try{
            // STEP 1 — Remove JWT from Cookie
            Cookie cookie = new Cookie(NAME, null);
            cookie.setMaxAge(0);              // ✅ expire immediately
            cookie.setHttpOnly(true);
            cookie.setPath("/");              // ✅ must match original cookie path
            response.addCookie(cookie);

            // STEP 2 — Invalidate session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();         // ✅ clear all session data
            }
            return ResponseEntity.ok(Map.of(
                    "status", true,
                    "message", "Logout successful",
                    "redirect","/login"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", false,
                    "message", "Something went wrong, Please try again later",
                    "error",e.getMessage()
            ));
        }
    }
}
