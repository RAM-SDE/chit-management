package com.chit_management.chit.controller.chitplan;


import com.chit_management.chit.dto.chit.ChitEnrollmentRequest;
import com.chit_management.chit.service.chit.ChitEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class ChitEnrollmentAPIController {

    private final ChitEnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "")  String search,
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "10") int limit) {

        int page = limit > 0 ? offset / limit : 0;
        var result = enrollmentService
                .getAllEnrollments(search, page, limit);

        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    @GetMapping("/customer/{customerUuid}")
    public ResponseEntity<?> byCustomer(
            @PathVariable String customerUuid) {
        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByCustomer(customerUuid));
    }

    @PostMapping
    public ResponseEntity<?> enroll(
            @Valid @RequestBody ChitEnrollmentRequest request) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Customer enrolled successfully",
                "data",    enrollmentService.enroll(request)
        ));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> withdraw(
            @PathVariable String uuid) {
        enrollmentService.withdraw(uuid);
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Enrollment withdrawn successfully"
        ));
    }
}
