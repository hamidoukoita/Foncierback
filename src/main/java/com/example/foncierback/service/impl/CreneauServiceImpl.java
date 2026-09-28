package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.CreneauRequest;
import com.example.foncierback.dto.response.CreneauResponse;
import com.example.foncierback.entity.Creneau;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.repository.CreneauRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.CreneauService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreneauServiceImpl implements CreneauService {

    private final CreneauRepository creneauRepository;
    private final SocietePromotriceRepository societePromotriceRepository;

    @Override
    public CreneauResponse create(CreneauRequest request) {
        SocietePromotrice societe = societePromotriceRepository.findById(request.getSocieteId())
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));

        Creneau creneau = Creneau.builder()
                .heure(request.getHeure())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .disponible(request.getDisponible() != null ? request.getDisponible() : true)
                .societePromotrice(societe)
                .build();

        Creneau saved = creneauRepository.save(creneau);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CreneauResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreneauResponse> getAll() {
        return creneauRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreneauResponse> getBySocieteId(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return creneauRepository.findBySocietePromotriceId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreneauResponse> getBySocieteIdAndDisponible(Long societeId, Boolean disponible) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return creneauRepository.findBySocietePromotriceIdAndDisponible(societeId, disponible)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CreneauResponse update(Long id, CreneauRequest request) {
        Creneau existing = findEntityById(id);

        if (request.getSocieteId() != null &&
                (existing.getSocietePromotrice() == null || !existing.getSocietePromotrice().getId().equals(request.getSocieteId()))) {
            SocietePromotrice societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
            existing.setSocietePromotrice(societe);
        }

        existing.setHeure(request.getHeure());
        existing.setHeureDebut(request.getHeureDebut());
        existing.setHeureFin(request.getHeureFin());
        if (request.getDisponible() != null) {
            existing.setDisponible(request.getDisponible());
        }

        Creneau updated = creneauRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Creneau existing = findEntityById(id);
        creneauRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Creneau findEntityById(Long id) {
        return creneauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable avec l'identifiant : " + id));
    }

    private CreneauResponse mapToResponse(Creneau entity) {
        return CreneauResponse.builder()
                .id(entity.getId())
                .heure(entity.getHeure())
                .heureDebut(entity.getHeureDebut())
                .heureFin(entity.getHeureFin())
                .disponible(entity.getDisponible())
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .societeNom(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getNom() : null)
                .build();
    }
}
