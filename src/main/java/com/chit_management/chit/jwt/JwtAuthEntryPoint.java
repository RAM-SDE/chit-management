package com.chit_management.chit.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        String uri = request.getRequestURI();
        log.warn("Unauthorized access: {}", uri);

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
}