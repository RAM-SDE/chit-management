package com.chit_management.chit.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ════════════════════════════════════════════
    // THYMELEAF TEMPLATE NOT FOUND
    // Catches: template might not exist errors
    // ════════════════════════════════════════════
    @ExceptionHandler(TemplateInputException.class)
    public ModelAndView handleTemplateNotFound(
            TemplateInputException ex,
            HttpServletRequest request) {

        log.warn("Template not found: {} — {}",
                request.getRequestURI(), ex.getMessage());

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", "404");
        mv.addObject("message",
                "Page not found. The requested page does not exist.");
        return mv;
    }

    // ════════════════════════════════════════════
    // VALIDATION — @Valid fails
    // ════════════════════════════════════════════
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex) {

        String firstField = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getField)
                .orElse("unknown");

        String firstError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        log.warn("Validation error: {} - {}", firstField, firstError);

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "field",  firstField,
                "error",  firstError
        ));
    }

    // ════════════════════════════════════════════
    // JWT EXPIRED
    // ════════════════════════════════════════════
    @ExceptionHandler(ExpiredJwtException.class)
    public Object handleSessionExpired(
            HttpServletRequest request) {

        log.warn("JWT expired for: {}", request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(401).body(Map.of(
                    "status",  401,
                    "error",   "Session expired",
                    "message", "Your session has expired. Please login again."
            ));
        }

        ModelAndView mv = new ModelAndView("session-expired");
        mv.addObject("code", "401");
        mv.addObject("message",
                "Your session has timed out. Please log in again.");
        return mv;
    }

    // ════════════════════════════════════════════
    // BAD CREDENTIALS
    // ════════════════════════════════════════════
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<?> handleBadCredentials() {
        return ResponseEntity.status(401).body(Map.of(
                "status",  401,
                "error",   "Invalid credentials",
                "message", "Invalid email or password"
        ));
    }

    // ════════════════════════════════════════════
    // USER NOT FOUND
    // ════════════════════════════════════════════
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserNotFound(
            UsernameNotFoundException ex) {
        return ResponseEntity.status(401).body(Map.of(
                "status",  401,
                "error",   "User not found",
                "message", ex.getMessage()
        ));
    }

    // ════════════════════════════════════════════
    // ACCOUNT DISABLED
    // ════════════════════════════════════════════
    @ExceptionHandler(DisabledException.class)
    @ResponseBody
    public ResponseEntity<?> handleDisabled() {
        return ResponseEntity.status(403).body(Map.of(
                "status",  403,
                "error",   "Account disabled",
                "message", "Your account has been disabled. Contact administrator."
        ));
    }

    // ════════════════════════════════════════════
    // RUNTIME EXCEPTION
    // ════════════════════════════════════════════
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Runtime error on {}: {}",
                request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error",  ex.getMessage()
            ));
        }

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", "500");
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    // ════════════════════════════════════════════
    // ILLEGAL ARGUMENT
    // ════════════════════════════════════════════
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgument(
            IllegalArgumentException ex) {

        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error",  ex.getMessage()
        ));
    }

    // ════════════════════════════════════════════
    // 404 — URL not found
    // ════════════════════════════════════════════
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResource(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.warn("404 Not found: {}", request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(404).body(Map.of(
                    "status",  404,
                    "error",   "Not found",
                    "message", "The requested resource was not found"
            ));
        }

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", "404");
        mv.addObject("message",
                "Page not found. The page you are looking for does not exist.");
        return mv;
    }

    // ════════════════════════════════════════════
    // NULL POINTER
    // ════════════════════════════════════════════
    @ExceptionHandler(NullPointerException.class)
    public Object handleNullPointer(
            NullPointerException ex,
            HttpServletRequest request) {

        log.error("Null pointer on {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        if (isApiRequest(request)) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "error",  "Something went wrong"
            ));
        }

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", "500");
        mv.addObject("message", "Something went wrong");
        return mv;
    }

    // ════════════════════════════════════════════
    // CATCH ALL
    // ════════════════════════════════════════════
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {

        // Check if it's a wrapped Thymeleaf error
        if (ex.getCause() instanceof TemplateInputException) {
            log.warn("Wrapped template error: {}", request.getRequestURI());
            ModelAndView mv = new ModelAndView("error-page");
            mv.addObject("code", "404");
            mv.addObject("message", "Page not found.");
            return mv;
        }

        Object statusCode = request.getAttribute(
                RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (statusCode != null) {
            status = HttpStatus.valueOf(
                    Integer.parseInt(statusCode.toString()));
        }

        String message = getMessage(status, ex);
        log.error("Unhandled exception on {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        if (isApiRequest(request)) {
            return ResponseEntity.status(status).body(Map.of(
                    "status", status.value(),
                    "error",  message
            ));
        }

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", String.valueOf(status.value()));
        mv.addObject("message", message);
        return mv;
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════
    private String getMessage(HttpStatus status, Exception ex) {
        return switch (status) {
            case BAD_REQUEST           -> "Bad request";
            case UNAUTHORIZED          -> "Unauthorized access";
            case FORBIDDEN             -> "Access denied";
            case NOT_FOUND             -> "Page not found";
            case INTERNAL_SERVER_ERROR -> "Something went wrong";
            default -> ex.getMessage() != null
                    ? ex.getMessage() : "Unknown error";
        };
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri         = request.getRequestURI();
        String contentType = request.getContentType();
        String accept      = request.getHeader("Accept");
        String xRequested  = request.getHeader("X-Requested-With");

        return uri.startsWith("/api/")
                || "XMLHttpRequest".equals(xRequested)
                || (contentType != null
                && contentType.contains("application/json"))
                || (accept != null
                && accept.contains("application/json"));
    }


    // ════════════════════════════════════════════
// ACCESS DENIED (403) — forwarded from Security filter
// ════════════════════════════════════════════
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.status(403).body(Map.of(
                    "status",  403,
                    "error",   "Access denied",
                    "message", "You do not have permission to access this resource."
            ));
        }

        ModelAndView mv = new ModelAndView("error-page");
        mv.addObject("code", "403");
        mv.addObject("message", "Access denied. You don't have permission to view this page.");
        return mv;
    }
}
