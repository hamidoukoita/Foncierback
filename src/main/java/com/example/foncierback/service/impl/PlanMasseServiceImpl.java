package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.PlanMasseRequest;
import com.example.foncierback.dto.response.PlanMasseResponse;
import com.example.foncierback.entity.PlanMasse;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.repository.PlanMasseRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.service.PlanMasseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanMasseServiceImpl implements PlanMasseService {

    private final PlanMasseRepository planMasseRepository;
    private final ProgrammeFoncierRepository programmeFoncierRepository;

    @Override
    public PlanMasseResponse create(PlanMasseRequest request) {
        ProgrammeFoncier programme = programmeFoncierRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + request.getProgrammeId()));

        if (planMasseRepository.existsByProgrammeFoncierId(request.getProgrammeId())) {
            throw new BadRequestException("Un plan de masse existe déjà pour le programme foncier ID : " + request.getProgrammeId());
        }

        PlanMasse planMasse = PlanMasse.builder()
                .plan(request.getPlan())
                .description(request.getDescription())
                .version(request.getVersion())
                .dateMiseAJour(LocalDateTime.now())
                .programmeFoncier(programme)
                .build();

        PlanMasse saved = planMasseRepository.save(planMasse);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanMasseResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PlanMasseResponse getByProgrammeId(Long programmeId) {
        if (!programmeFoncierRepository.existsById(programmeId)) {
            throw new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + programmeId);
        }

        PlanMasse planMasse = planMasseRepository.findByProgrammeFoncierId(programmeId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun plan de masse trouvé pour le programme foncier ID : " + programmeId));

        return mapToResponse(planMasse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanMasseResponse> getAll() {
        return planMasseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PlanMasseResponse update(Long id, PlanMasseRequest request) {
        PlanMasse existing = findEntityById(id);

        if (request.getProgrammeId() != null &&
                (existing.getProgrammeFoncier() == null || !existing.getProgrammeFoncier().getId().equals(request.getProgrammeId()))) {
            if (planMasseRepository.existsByProgrammeFoncierId(request.getProgrammeId())) {
                throw new BadRequestException("Un plan de masse existe déjà pour le programme foncier ID : " + request.getProgrammeId());
            }

            ProgrammeFoncier programme = programmeFoncierRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + request.getProgrammeId()));
            existing.setProgrammeFoncier(programme);
        }

        existing.setPlan(request.getPlan());
        existing.setDescription(request.getDescription());
        existing.setVersion(request.getVersion());
        existing.setDateMiseAJour(LocalDateTime.now());

        PlanMasse updated = planMasseRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        PlanMasse existing = findEntityById(id);
        planMasseRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanMasse findEntityById(Long id) {
        return planMasseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de masse introuvable avec l'identifiant : " + id));
    }

    private PlanMasseResponse mapToResponse(PlanMasse entity) {
        return PlanMasseResponse.builder()
                .id(entity.getId())
                .plan(entity.getPlan())
                .description(entity.getDescription())
                .version(entity.getVersion())
                .dateMiseAJour(entity.getDateMiseAJour())
                .programmeId(entity.getProgrammeFoncier() != null ? entity.getProgrammeFoncier().getId() : null)
                .programmeNom(entity.getProgrammeFoncier() != null ? entity.getProgrammeFoncier().getNom() : null)
                .build();
    }
}
