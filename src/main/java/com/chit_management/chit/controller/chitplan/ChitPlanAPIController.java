package com.chit_management.chit.controller.chitplan;


import com.chit_management.chit.dto.chit.ChitPlanRequest;
import com.chit_management.chit.dto.chit.ChitPlanResponse;
import com.chit_management.chit.entity.chit.ChitPlan;
import com.chit_management.chit.service.chit.ChitPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chit-plans")
@RequiredArgsConstructor
public class ChitPlanAPIController {

    private final ChitPlanService chitPlanService;

    // ── Bootstrap-table server-side ───────────
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "")  String search,
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "10") int limit) {

        int page = limit > 0 ? offset / limit : 0;
        Page<ChitPlanResponse> result =
                chitPlanService.getPlans(search, page, limit);

        return ResponseEntity.ok(Map.of(
                "total", result.getTotalElements(),
                "rows",  result.getContent()
        ));
    }

    // ── Get single plan ───────────────────────
    @GetMapping("/{uuid}")
    public ResponseEntity<?> get(@PathVariable String uuid) {
        return ResponseEntity.ok(
                chitPlanService.getPlanByUuid(uuid));
    }

    // ── Save ──────────────────────────────────
    @PostMapping
    public ResponseEntity<?> save(
            @Validated @RequestBody ChitPlanRequest dto) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Chit plan created successfully",
                "data",    chitPlanService.savePlan(dto)
        ));
    }

    // ── Update ────────────────────────────────
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @PathVariable String uuid,
            @Validated @RequestBody ChitPlanRequest dto) {
        return ResponseEntity.ok(Map.of(
                "status",  true,
                "message", "Chit plan updated successfully",
                "data",    chitPlanService.updatePlan(uuid, dto)
        ));
    }

    // ── Update Status ─────────────────────────
    @PatchMapping("/{uuid}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String uuid,
            @RequestParam String status) {
        try {
            ChitPlan.Status newStatus =
                    ChitPlan.Status.valueOf(status.toUpperCase());
            chitPlanService.updateStatus(uuid, newStatus);
            return ResponseEntity.ok(Map.of(
                    "status",  true,
                    "message", "Status updated to " + newStatus
            ));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }


    // ── Soft Delete ───────────────────────────
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(
            @PathVariable String uuid) {

        chitPlanService.deactivateChitPlan(uuid);

        return ResponseEntity.ok(Map.of(
                "status", true,
                "message",
                "Chit plan deactivated successfully"
        ));
    }

    @PatchMapping("/{uuid}/activate")
    public ResponseEntity<?> activate(
            @PathVariable String uuid) {

        chitPlanService.activateChitPlan(uuid);

        return ResponseEntity.ok(Map.of(
                "status", true,
                "message", "Chit plan activated successfully"
        ));
    }
}