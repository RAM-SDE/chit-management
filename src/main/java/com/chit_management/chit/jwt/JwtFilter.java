package com.chit_management.chit.jwt;


import com.chit_management.chit.security.CustomUserDetails;
import com.chit_management.chit.serviceImpl.CustomUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;

    @Value("${jwt.name}")
    private String NAME;

    private static final List<String> PUBLIC_URLS = List.of(
            "/login", "/login/",
            "/error-page",
            "/error",
            "/session-expired",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/sse/connect",
            "/css/", "/js/", "/images/", "/favicon.ico",
            "/webfonts/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // STEP 1: Public URLs — skip everything
        if (isPublicUrl(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // STEP 2: Read JWT from cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (NAME.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // STEP 3: Login page — redirect to home if already logged in
        if (uri.equals("/login") || uri.equals("/login/")) {
            if (token != null && jwtUtil.validateToken(token)) {
                response.sendRedirect("/");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // STEP 4: No token or invalid token
        if (token == null || !jwtUtil.validateToken(token)) {
            handleUnauthenticated(request, response, uri);
            return;
        }

        // STEP 5: Valid token → load user and set authentication
        try {
            String email = jwtUtil.extractUsername(token);
            CustomUserDetails userDetails = (CustomUserDetails)
                    userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            logger.error("JWT load error: " + e.getMessage());
            SecurityContextHolder.clearContext();
            handleUnauthenticated(request, response, uri);
            return;
        }

        // STEP 6: Continue — Spring Security handles authorization
        filterChain.doFilter(request, response);
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════
    private void handleUnauthenticated(HttpServletRequest request,
                                       HttpServletResponse response,
                                       String uri) throws IOException {
        boolean isApi = uri.startsWith("/api/")
                || "XMLHttpRequest".equals(
                request.getHeader("X-Requested-With"));
        if (isApi) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"status\":401," +
                            "\"error\":\"Unauthorized\"," +
                            "\"message\":\"Session expired. Please login again.\"}");
        } else {
            response.sendRedirect("/session-expired");
        }
    }

    private boolean isPublicUrl(String uri) {
        return PUBLIC_URLS.stream().anyMatch(uri::startsWith);
    }
}
