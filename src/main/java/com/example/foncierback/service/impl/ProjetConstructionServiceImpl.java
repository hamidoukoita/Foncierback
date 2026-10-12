package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.AvancementProjetRequest;
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

        Acquereur acquereur = resolveAcquereur(request.getAcquereurId());
        ModelMaison modelMaison = resolveModel(request.getModelMaisonId());
        SocietePromotrice societe = resolveSociete(request.getSocieteId());
        AgentPromoteur agent = resolveAgent(request.getAgentId());

        Integer progression = request.getProgression() != null ? request.getProgression() : 0;
        if (progression < 0 || progression > 100) throw new BadRequestException("La progression doit être comprise entre 0 et 100");
        StatutProjet statut = request.getStatut() != null ? request.getStatut() : StatutProjet.EN_ETUDE;

        ProjetConstruction projet = ProjetConstruction.builder()
                .numeroDossier(numeroDossier.trim())
                .numeroTitreFoncier(request.getNumeroTitreFoncier())
                .localisationTerrain(request.getLocalisationTerrain())
                .superficieTerrain(request.getSuperficieTerrain())
                .typeTerrain(clean(request.getTypeTerrain()))
                .description(request.getDescription())
                .budgetEstime(request.getBudgetEstime())
                .documentTfUrl(request.getDocumentTfUrl())
                .statut(statut)
                .progression(progression)
                .etapeAvancement(clean(request.getEtapeAvancement()) != null ? clean(request.getEtapeAvancement()) : "Dossier reçu")
                .commentaireAvancement(request.getCommentaireAvancement())
                .dateDemande(request.getDateDemande() != null ? request.getDateDemande() : LocalDateTime.now())
                .acquereur(acquereur)
                .modelMaison(modelMaison)
                .societePromotrice(societe)
                .agentPromoteur(agent)
                .build();

        return mapToResponse(projetConstructionRepository.save(projet));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetConstructionResponse getById(Long id) { return mapToResponse(findEntityById(id)); }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> getAll() {
        return projetConstructionRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public ProjetConstructionResponse update(Long id, ProjetConstructionRequest request) {
        ProjetConstruction existing = findEntityById(id);

        if (request.getNumeroDossier() != null && !request.getNumeroDossier().isBlank()
                && !existing.getNumeroDossier().equalsIgnoreCase(request.getNumeroDossier())) {
            if (projetConstructionRepository.existsByNumeroDossier(request.getNumeroDossier())) {
                throw new BadRequestException("Un projet avec le numéro de dossier '" + request.getNumeroDossier() + "' existe déjà");
            }
            existing.setNumeroDossier(request.getNumeroDossier().trim());
        }

        existing.setNumeroTitreFoncier(request.getNumeroTitreFoncier());
        existing.setLocalisationTerrain(request.getLocalisationTerrain());
        existing.setSuperficieTerrain(request.getSuperficieTerrain());
        existing.setTypeTerrain(clean(request.getTypeTerrain()));
        existing.setDescription(request.getDescription());
        existing.setBudgetEstime(request.getBudgetEstime());
        existing.setDocumentTfUrl(request.getDocumentTfUrl());

        if (request.getStatut() != null) existing.setStatut(request.getStatut());
        if (request.getProgression() != null) existing.setProgression(request.getProgression());
        if (request.getEtapeAvancement() != null) existing.setEtapeAvancement(clean(request.getEtapeAvancement()));
        if (request.getCommentaireAvancement() != null) existing.setCommentaireAvancement(request.getCommentaireAvancement());
        if (request.getDateDemande() != null) existing.setDateDemande(request.getDateDemande());

        if (request.getAcquereurId() != null) existing.setAcquereur(resolveAcquereur(request.getAcquereurId()));
        if (request.getModelMaisonId() != null) existing.setModelMaison(resolveModel(request.getModelMaisonId()));
        if (request.getSocieteId() != null) existing.setSocietePromotrice(resolveSociete(request.getSocieteId()));
        if (request.getAgentId() != null) existing.setAgentPromoteur(resolveAgent(request.getAgentId()));
        existing.setDateDerniereMiseAJour(LocalDateTime.now());

        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public void delete(Long id) { projetConstructionRepository.delete(findEntityById(id)); }

    @Override
    public ProjetConstructionResponse valider(Long id) {
        ProjetConstruction existing = findEntityById(id);
        existing.validerProjet();
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse refuser(Long id, String motifRefus) {
        ProjetConstruction existing = findEntityById(id);
        existing.refuserProjet(motifRefus);
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse changerModele(Long id, Long modelMaisonId) {
        ProjetConstruction existing = findEntityById(id);
        ModelMaison model = resolveModel(modelMaisonId);
        if (model == null) throw new BadRequestException("Le modèle de maison est obligatoire");
        if (model.getSurfaceTerrainMin() != null && existing.getSuperficieTerrain() < model.getSurfaceTerrainMin()) {
            throw new BadRequestException("Ce modèle nécessite au minimum " + model.getSurfaceTerrainMin() + " m² de terrain");
        }
        if (model.getSurfaceTerrainMax() != null && existing.getSuperficieTerrain() > model.getSurfaceTerrainMax()) {
            throw new BadRequestException("Ce modèle est prévu pour un terrain de " + model.getSurfaceTerrainMax() + " m² maximum");
        }
        if (model.getTypeTerrainCompatible() != null && existing.getTypeTerrain() != null
                && !model.getTypeTerrainCompatible().equalsIgnoreCase(existing.getTypeTerrain())) {
            throw new BadRequestException("Le modèle sélectionné n'est pas compatible avec le type de terrain renseigné");
        }
        existing.setModelMaison(model);
        existing.setDateDerniereMiseAJour(LocalDateTime.now());
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse mettreAJourAvancement(Long id, AvancementProjetRequest request) {
        ProjetConstruction existing = findEntityById(id);
        existing.mettreAJourAvancement(request.getProgression(), request.getEtapeAvancement(), request.getCommentaireAvancement());
        if (request.getProgression() == 100 && existing.getStatut() == StatutProjet.ACCEPTER) {
            existing.setEtapeAvancement("Travaux terminés");
        }
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> findByAcquereur(Long acquereurId) {
        return projetConstructionRepository.findByAcquereurId(acquereurId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> findBySociete(Long societeId) {
        return projetConstructionRepository.findBySocietePromotriceId(societeId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetConstruction findEntityById(Long id) {
        return projetConstructionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet de construction introuvable avec l'identifiant : " + id));
    }

    private Acquereur resolveAcquereur(Long id) {
        return id == null ? null : acquereurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + id));
    }
    private ModelMaison resolveModel(Long id) {
        return id == null ? null : modelMaisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + id));
    }
    private SocietePromotrice resolveSociete(Long id) {
        return id == null ? null : societePromotriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + id));
    }
    private AgentPromoteur resolveAgent(Long id) {
        return id == null ? null : agentPromoteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + id));
    }
    private String clean(String v) {
        if (v == null) return null; String t=v.trim(); return t.isEmpty()?null:t;
    }

    private ProjetConstructionResponse mapToResponse(ProjetConstruction e) {
        String acquereurNom = e.getAcquereur() == null ? null : (e.getAcquereur().getPrenom() + " " + e.getAcquereur().getNom()).trim();
        String agentNom = e.getAgentPromoteur() == null ? null : (e.getAgentPromoteur().getPrenom() + " " + e.getAgentPromoteur().getNom()).trim();
        return ProjetConstructionResponse.builder()
                .id(e.getId())
                .numeroDossier(e.getNumeroDossier())
                .numeroTitreFoncier(e.getNumeroTitreFoncier())
                .localisationTerrain(e.getLocalisationTerrain())
                .superficieTerrain(e.getSuperficieTerrain())
                .typeTerrain(e.getTypeTerrain())
                .description(e.getDescription())
                .budgetEstime(e.getBudgetEstime())
                .documentTfUrl(e.getDocumentTfUrl())
                .statut(e.getStatut())
                .progression(e.getProgression())
                .etapeAvancement(e.getEtapeAvancement())
                .commentaireAvancement(e.getCommentaireAvancement())
                .dateDerniereMiseAJour(e.getDateDerniereMiseAJour())
                .motifRefus(e.getMotifRefus())
                .dateDemande(e.getDateDemande())
                .dateTraitement(e.getDateTraitement())
                .acquereurId(e.getAcquereur() != null ? e.getAcquereur().getId() : null)
                .acquereurNom(acquereurNom)
                .acquereurTelephone(e.getAcquereur() != null ? e.getAcquereur().getTelephone() : null)
                .modelMaisonId(e.getModelMaison() != null ? e.getModelMaison().getId() : null)
                .modelMaisonLibeller(e.getModelMaison() != null ? e.getModelMaison().getLibeller() : null)
                .modelMaisonImageUrl(e.getModelMaison() != null ? e.getModelMaison().getImageUrl() : null)
                .modelMaisonPlanUrl(e.getModelMaison() != null ? e.getModelMaison().getPlanUrl() : null)
                .societeId(e.getSocietePromotrice() != null ? e.getSocietePromotrice().getId() : null)
                .societeNom(e.getSocietePromotrice() != null ? e.getSocietePromotrice().getNom() : null)
                .agentId(e.getAgentPromoteur() != null ? e.getAgentPromoteur().getId() : null)
                .agentNom(agentNom)
                .build();
    }
}
