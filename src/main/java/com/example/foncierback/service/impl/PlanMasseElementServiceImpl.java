package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.PlanMasseElementRequest;
import com.example.foncierback.dto.response.PlanMasseElementResponse;
import com.example.foncierback.entity.PlanMasse;
import com.example.foncierback.entity.PlanMasseElement;
import com.example.foncierback.repository.PlanMasseElementRepository;
import com.example.foncierback.repository.PlanMasseRepository;
import com.example.foncierback.service.PlanMasseElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanMasseElementServiceImpl implements PlanMasseElementService {

    private final PlanMasseElementRepository elementRepository;
    private final PlanMasseRepository planMasseRepository;

    @Override
    public PlanMasseElementResponse create(PlanMasseElementRequest request) {
        PlanMasse plan = findPlan(request.getPlanMasseId());
        PlanMasseElement element = PlanMasseElement.builder()
                .type(request.getType())
                .nom(request.getNom())
                .description(request.getDescription())
                .geometryJson(request.getGeometryJson())
                .styleJson(request.getStyleJson())
                .visible(request.getVisible() == null || request.getVisible())
                .zIndex(request.getZIndex() == null ? 0 : request.getZIndex())
                .planMasse(plan)
                .build();
        return map(elementRepository.save(element));
    }

    @Override
    @Transactional(readOnly = true)
    public PlanMasseElementResponse getById(Long id) {
        return map(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanMasseElementResponse> getByPlanMasseId(Long planMasseId) {
        findPlan(planMasseId);
        return elementRepository.findByPlanMasseIdOrderByZIndexAscIdAsc(planMasseId)
                .stream().map(this::map).toList();
    }

    @Override
    public PlanMasseElementResponse update(Long id, PlanMasseElementRequest request) {
        PlanMasseElement existing = findEntity(id);
        PlanMasse plan = findPlan(request.getPlanMasseId());
        existing.setType(request.getType());
        existing.setNom(request.getNom());
        existing.setDescription(request.getDescription());
        existing.setGeometryJson(request.getGeometryJson());
        existing.setStyleJson(request.getStyleJson());
        existing.setVisible(request.getVisible() == null || request.getVisible());
        existing.setZIndex(request.getZIndex() == null ? 0 : request.getZIndex());
        existing.setPlanMasse(plan);
        return map(elementRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        elementRepository.delete(findEntity(id));
    }

    private PlanMasse findPlan(Long id) {
        return planMasseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de masse introuvable avec l'identifiant : " + id));
    }

    private PlanMasseElement findEntity(Long id) {
        return elementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Élément du plan de masse introuvable avec l'identifiant : " + id));
    }

    private PlanMasseElementResponse map(PlanMasseElement e) {
        return PlanMasseElementResponse.builder()
                .id(e.getId())
                .type(e.getType())
                .nom(e.getNom())
                .description(e.getDescription())
                .geometryJson(e.getGeometryJson())
                .styleJson(e.getStyleJson())
                .visible(e.getVisible())
                .zIndex(e.getZIndex())
                .planMasseId(e.getPlanMasse() != null ? e.getPlanMasse().getId() : null)
                .build();
    }
}
