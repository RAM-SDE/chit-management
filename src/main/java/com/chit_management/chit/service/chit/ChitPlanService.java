package com.chit_management.chit.service.chit;

import com.chit_management.chit.dto.chit.ChitPlanRequest;
import com.chit_management.chit.dto.chit.ChitPlanResponse;
import com.chit_management.chit.entity.chit.ChitPlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChitPlanService {

    Page<ChitPlanResponse> getPlans(String search, int page, int size);
    ChitPlanResponse getPlanByUuid(String uuid);
    ChitPlanResponse savePlan(ChitPlanRequest dto);
    ChitPlanResponse updatePlan(String uuid, ChitPlanRequest dto);
    void updateStatus(String uuid, ChitPlan.Status status);
    long getTotalActivePlans();

    void deactivateChitPlan(
            String uuid
    );

    void activateChitPlan(String uuid);

}