package com.chit_management.chit.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException, ServletException {

        log.warn("Access denied: {}", request.getRequestURI());

        boolean isApi = request.getRequestURI().startsWith("/api/")
                || "XMLHttpRequest".equals(
                request.getHeader("X-Requested-With"));

        if (isApi) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"status\":403," +
                            "\"error\":\"Access denied\"," +
                            "\"message\":\"You do not have permission to access this resource.\"}");
        } else {
            request.setAttribute("jakarta.servlet.error.exception", ex);
            request.setAttribute("jakarta.servlet.error.status_code", 403);
            request.getRequestDispatcher("/error")
                    .forward(request, response);
        }
    }
}