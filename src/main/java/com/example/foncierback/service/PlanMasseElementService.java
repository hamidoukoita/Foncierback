package com.example.foncierback.service;

import com.example.foncierback.dto.request.PlanMasseElementRequest;
import com.example.foncierback.dto.response.PlanMasseElementResponse;

import java.util.List;

public interface PlanMasseElementService {
    PlanMasseElementResponse create(PlanMasseElementRequest request);
    PlanMasseElementResponse getById(Long id);
    List<PlanMasseElementResponse> getByPlanMasseId(Long planMasseId);
    PlanMasseElementResponse update(Long id, PlanMasseElementRequest request);
    void delete(Long id);
}
