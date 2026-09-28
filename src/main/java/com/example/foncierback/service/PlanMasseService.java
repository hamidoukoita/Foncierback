package com.example.foncierback.service;

import com.example.foncierback.dto.request.PlanMasseRequest;
import com.example.foncierback.dto.response.PlanMasseResponse;
import com.example.foncierback.entity.PlanMasse;

import java.util.List;

public interface PlanMasseService {

    PlanMasseResponse create(PlanMasseRequest request);

    PlanMasseResponse getById(Long id);

    PlanMasseResponse getByProgrammeId(Long programmeId);

    List<PlanMasseResponse> getAll();

    PlanMasseResponse update(Long id, PlanMasseRequest request);

    void delete(Long id);

    PlanMasse findEntityById(Long id);
}
