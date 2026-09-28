package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ProjetConstructionRequest;
import com.example.foncierback.dto.response.ProjetConstructionResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.ModelMaison;
import com.example.foncierback.entity.ProjetConstruction;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutProjet;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.ModelMaisonRepository;
import com.example.foncierback.repository.ProjetConstructionRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ProjetConstructionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetConstructionServiceImpl implements ProjetConstructionService {

    private final ProjetConstructionRepository projetConstructionRepository;
    private final AcquereurRepository acquereurRepository;
    private final ModelMaisonRepository modelMaisonRepository;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;

    @Override
    public ProjetConstructionResponse create(ProjetConstructionRequest request) {
        String numeroDossier = request.getNumeroDossier();
        if (numeroDossier == null || numeroDossier.isBlank()) {
            numeroDossier = "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (projetConstructionRepository.existsByNumeroDossier(numeroDossier)) {
            throw new BadRequestException("Un projet avec le numéro de dossier '" + numeroDossier + "' existe déjà");
        }

        Acquereur acquereur = null;
        if (request.getAcquereurId() != null) {
            acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
        }

        ModelMaison modelMaison = null;
        if (request.getModelMaisonId() != null) {
            modelMaison = modelMaisonRepository.findById(request.getModelMaisonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + request.getModelMaisonId()));
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
        }

        AgentPromoteur agent = null;
        if (request.getAgentId() != null) {
            agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
        }

        LocalDateTime dateDemande = request.getDateDemande() != null ? request.getDateDemande() : LocalDateTime.now();
        StatutProjet statut = request.getStatut() != null ? request.getStatut() : StatutProjet.EN_ETUDE;

        ProjetConstruction projet = ProjetConstruction.builder()
                .numeroDossier(numeroDossier)
                .numeroTitreFoncier(request.getNumeroTitreFoncier())
                .localisationTerrain(request.getLocalisationTerrain())
                .superficieTerrain(request.getSuperficieTerrain())
                .description(request.getDescription())
                .budgetEstime(request.getBudgetEstime())
                .documentTfUrl(request.getDocumentTfUrl())
                .statut(statut)
                .motifRefus(request.getMotifRefus())
                .dateDemande(dateDemande)
                .acquereur(acquereur)
                .modelMaison(modelMaison)
                .societePromotrice(societe)
                .agentPromoteur(agent)
                .build();

        ProjetConstruction saved = projetConstructionRepository.save(projet);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetConstructionResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> getAll() {
        return projetConstructionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProjetConstructionResponse update(Long id, ProjetConstructionRequest request) {
        ProjetConstruction existing = findEntityById(id);

        if (request.getNumeroDossier() != null && !request.getNumeroDossier().isBlank()
                && !existing.getNumeroDossier().equalsIgnoreCase(request.getNumeroDossier())) {
            if (projetConstructionRepository.existsByNumeroDossier(request.getNumeroDossier())) {
                throw new BadRequestException("Un projet avec le numéro de dossier '" + request.getNumeroDossier() + "' existe déjà");
            }
            existing.setNumeroDossier(request.getNumeroDossier());
        }

        existing.setNumeroTitreFoncier(request.getNumeroTitreFoncier());
        existing.setLocalisationTerrain(request.getLocalisationTerrain());
        existing.setSuperficieTerrain(request.getSuperficieTerrain());
        existing.setDescription(request.getDescription());
        existing.setBudgetEstime(request.getBudgetEstime());
        existing.setDocumentTfUrl(request.getDocumentTfUrl());

        if (request.getStatut() != null) {
            existing.setStatut(request.getStatut());
        }

        existing.setMotifRefus(request.getMotifRefus());

        if (request.getDateDemande() != null) {
            existing.setDateDemande(request.getDateDemande());
        }

        if (request.getAcquereurId() != null) {
            Acquereur acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
            existing.setAcquereur(acquereur);
        } else {
            existing.setAcquereur(null);
        }

        if (request.getModelMaisonId() != null) {
            ModelMaison modelMaison = modelMaisonRepository.findById(request.getModelMaisonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + request.getModelMaisonId()));
            existing.setModelMaison(modelMaison);
        } else {
            existing.setModelMaison(null);
        }

        if (request.getSocieteId() != null) {
            SocietePromotrice societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
            existing.setSocietePromotrice(societe);
        } else {
            existing.setSocietePromotrice(null);
        }

        if (request.getAgentId() != null) {
            AgentPromoteur agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
            existing.setAgentPromoteur(agent);
        } else {
            existing.setAgentPromoteur(null);
        }

        ProjetConstruction updated = projetConstructionRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        ProjetConstruction existing = findEntityById(id);
        projetConstructionRepository.delete(existing);
    }

    @Override
    public ProjetConstructionResponse valider(Long id) {
        ProjetConstruction existing = findEntityById(id);
        existing.validerProjet();
        ProjetConstruction saved = projetConstructionRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public ProjetConstructionResponse refuser(Long id, String motifRefus) {
        ProjetConstruction existing = findEntityById(id);
        existing.refuserProjet(motifRefus);
        ProjetConstruction saved = projetConstructionRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> findByAcquereur(Long acquereurId) {
        return projetConstructionRepository.findByAcquereurId(acquereurId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> findBySociete(Long societeId) {
        return projetConstructionRepository.findBySocietePromotriceId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetConstruction findEntityById(Long id) {
        return projetConstructionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet de construction introuvable avec l'identifiant : " + id));
    }

    private ProjetConstructionResponse mapToResponse(ProjetConstruction entity) {
        return ProjetConstructionResponse.builder()
                .id(entity.getId())
                .numeroDossier(entity.getNumeroDossier())
                .numeroTitreFoncier(entity.getNumeroTitreFoncier())
                .localisationTerrain(entity.getLocalisationTerrain())
                .superficieTerrain(entity.getSuperficieTerrain())
                .description(entity.getDescription())
                .budgetEstime(entity.getBudgetEstime())
                .documentTfUrl(entity.getDocumentTfUrl())
                .statut(entity.getStatut())
                .motifRefus(entity.getMotifRefus())
                .dateDemande(entity.getDateDemande())
                .dateTraitement(entity.getDateTraitement())
                .acquereurId(entity.getAcquereur() != null ? entity.getAcquereur().getId() : null)
                .modelMaisonId(entity.getModelMaison() != null ? entity.getModelMaison().getId() : null)
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .agentId(entity.getAgentPromoteur() != null ? entity.getAgentPromoteur().getId() : null)
                .build();
    }
}
