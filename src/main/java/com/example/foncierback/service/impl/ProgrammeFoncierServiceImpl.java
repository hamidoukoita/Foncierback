package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ProgrammeFoncierRequest;
import com.example.foncierback.dto.response.ProgrammeFoncierResponse;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutProgramme;
import com.example.foncierback.repository.LotProgrammeRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ProgrammeFoncierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgrammeFoncierServiceImpl implements ProgrammeFoncierService {

    private final ProgrammeFoncierRepository programmeFoncierRepository;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final LotProgrammeRepository lotProgrammeRepository;

    @Override
    public ProgrammeFoncierResponse create(ProgrammeFoncierRequest request) {
        if (programmeFoncierRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Un programme foncier avec le nom '" + request.getNom() + "' existe déjà");
        }
        if (programmeFoncierRepository.existsByNumeroTitreMere(request.getNumeroTitreMere())) {
            throw new BadRequestException("Un programme foncier avec le titre mère '" + request.getNumeroTitreMere() + "' existe déjà");
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
        }

        ProgrammeFoncier programme = ProgrammeFoncier.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .lieu(request.getLieu())
                .numeroTitreMere(request.getNumeroTitreMere())
                .superficieTotale(request.getSuperficieTotale())
                .statut(request.getStatut())
                .dateCreation(LocalDateTime.now())
                .avancement(request.getAvancement() != null ? request.getAvancement() : 0)
                .eauSomapep(Boolean.TRUE.equals(request.getEauSomapep()))
                .electriciteEdm(Boolean.TRUE.equals(request.getElectriciteEdm()))
                .voirieBitumee(Boolean.TRUE.equals(request.getVoirieBitumee()))
                .societePromotrice(societe)
                .build();

        ProgrammeFoncier saved = programmeFoncierRepository.save(programme);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgrammeFoncierResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammeFoncierResponse> getAll() {
        return programmeFoncierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProgrammeFoncierResponse update(Long id, ProgrammeFoncierRequest request) {
        ProgrammeFoncier existing = findEntityById(id);

        if (!existing.getNom().equalsIgnoreCase(request.getNom())
                && programmeFoncierRepository.existsByNomAndIdNot(request.getNom(), id)) {
            throw new BadRequestException("Un programme foncier avec le nom '" + request.getNom() + "' existe déjà");
        }
        if (!existing.getNumeroTitreMere().equalsIgnoreCase(request.getNumeroTitreMere())
                && programmeFoncierRepository.existsByNumeroTitreMereAndIdNot(request.getNumeroTitreMere(), id)) {
            throw new BadRequestException("Un programme foncier avec le titre mère '" + request.getNumeroTitreMere() + "' existe déjà");
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
        }

        existing.setNom(request.getNom());
        existing.setDescription(request.getDescription());
        existing.setLieu(request.getLieu());
        existing.setNumeroTitreMere(request.getNumeroTitreMere());
        existing.setSuperficieTotale(request.getSuperficieTotale());
        existing.setStatut(request.getStatut());
        if (request.getAvancement() != null) {
            existing.setAvancement(request.getAvancement());
        }
        existing.setEauSomapep(Boolean.TRUE.equals(request.getEauSomapep()));
        existing.setElectriciteEdm(Boolean.TRUE.equals(request.getElectriciteEdm()));
        existing.setVoirieBitumee(Boolean.TRUE.equals(request.getVoirieBitumee()));
        existing.setSocietePromotrice(societe);

        ProgrammeFoncier updated = programmeFoncierRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        ProgrammeFoncier existing = findEntityById(id);
        programmeFoncierRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammeFoncierResponse> search(String keyword) {
        return programmeFoncierRepository.findByNomContainingIgnoreCaseOrLieuContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammeFoncierResponse> filterByStatut(StatutProgramme statut) {
        return programmeFoncierRepository.findByStatut(statut)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammeFoncierResponse> findBySociete(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return programmeFoncierRepository.findBySocietePromotriceId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgrammeFoncier findEntityById(Long id) {
        return programmeFoncierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + id));
    }

    private ProgrammeFoncierResponse mapToResponse(ProgrammeFoncier entity) {
        int totalLots = (int) lotProgrammeRepository.countByProgrammeFoncierId(entity.getId());
        return ProgrammeFoncierResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .description(entity.getDescription())
                .lieu(entity.getLieu())
                .numeroTitreMere(entity.getNumeroTitreMere())
                .superficieTotale(entity.getSuperficieTotale())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .avancement(entity.getAvancement())
                .eauSomapep(entity.getEauSomapep())
                .electriciteEdm(entity.getElectriciteEdm())
                .voirieBitumee(entity.getVoirieBitumee())
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .societeNom(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getNom() : null)
                .totalLots(totalLots)
                .planMasseId(entity.getPlanMasse() != null ? entity.getPlanMasse().getId() : null)
                .build();
    }
}
