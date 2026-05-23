package com.chit_management.chit.service.chit;

import com.chit_management.chit.dto.chit.ChitEnrollmentResponse;
import com.chit_management.chit.dto.chit.ChitEnrollmentRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChitEnrollmentService {
    Page<ChitEnrollmentResponse> getAllEnrollments(String search,
                                                   int page, int size);
    ChitEnrollmentResponse enroll(ChitEnrollmentRequest request);
    void withdraw(String uuid);
    List<ChitEnrollmentResponse> getEnrollmentsByCustomer(
            String customerUuid);
}
