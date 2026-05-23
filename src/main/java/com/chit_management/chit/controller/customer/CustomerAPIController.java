package com.chit_management.chit.controller.customer;

import com.chit_management.chit.dto.customer.CustomerDTO;
import com.chit_management.chit.dto.customer.CustomerResponseDTO;
import com.chit_management.chit.service.customer.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerAPIController {

    private final CustomerService customerService;

    // ── Bootstrap-table server-side ───────────
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        // bootstrap-table sends offset (row number), not page number
        int page = limit > 0 ? offset / limit : 0;
        Page<CustomerResponseDTO> result =
                customerService.getCustomers(search, page, limit);

        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    // ── Get single customer ───────────────────
    @GetMapping("/{uuid}")
    public ResponseEntity<?> get(@PathVariable String uuid) {
        return ResponseEntity.ok(customerService.getCustomerByUuid(uuid));
    }

    // ── Save ──────────────────────────────────
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Customer saved successfully",
                "data",    customerService.saveCustomer(dto)
        ));
    }

    // ── Update ────────────────────────────────
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable String uuid,
                                    @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Customer updated successfully",
                "data",    customerService.updateCustomer(uuid, dto)
        ));
    }

    // ── Deactivate ────────────────────────────
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(@PathVariable String uuid) {
        customerService.deactivateCustomer(uuid);
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Customer deactivated successfully"
        ));
    }

    // ── Activate ──────────────────────────────────
    @PatchMapping("/{uuid}/activate")
    public ResponseEntity<?> activate(@PathVariable String uuid) {
        customerService.activateCustomer(uuid);
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Customer activated successfully"
        ));
    }
}
