package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.AvancementProjetRequest;
import com.example.foncierback.dto.request.ProjetConstructionRequest;
import com.example.foncierback.dto.response.ProjetConstructionResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.ModelMaison;
import com.example.foncierback.entity.MaisonCleEnMain;
import com.example.foncierback.repository.MaisonCleEnMainRepository;
import com.example.foncierback.service.CouvertureResolver;
import com.example.foncierback.entity.ProjetConstruction;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutProjet;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.ModelMaisonRepository;
import com.example.foncierback.repository.ProjetConstructionRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ProjetConstructionAcces;
import com.example.foncierback.service.ProjetConstructionService;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.entity.enums.StatutAgrement;
import java.util.Objects;
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
    private final ProjetConstructionAcces acces;
    private final MaisonCleEnMainRepository maisonRepository;
    private final CouvertureResolver couvertureResolver;

    /**
     * Soumission d'un projet par un acquéreur : le dossier part chez une société agréée,
     * avec un modèle de son catalogue ou un modèle personnalisé décrit par l'acquéreur.
     * Statut, avancement et agent sont toujours initialisés côté serveur.
     */
    @Override
    public ProjetConstructionResponse create(ProjetConstructionRequest request) {
        Long acquereurId = SecurityUtils.isAdmin() ? request.getAcquereurId() : SecurityUtils.getCurrentUserId();
        Acquereur acquereur = resolveAcquereur(acquereurId);
        if (acquereur == null) throw new BadRequestException("L'acquéreur du projet est obligatoire");

        SocietePromotrice societe = resolveSociete(request.getSocieteId());
        if (societe == null) throw new BadRequestException("Choisissez la société promotrice qui réalisera le projet");
        if (societe.getStatutAgrement() != StatutAgrement.VERIFIER) {
            throw new BadRequestException("Cette société n'est pas encore agréée : elle ne peut pas recevoir de projet");
        }

        ModelMaison modelMaison = resolveModel(request.getModelMaisonId());
        MaisonCleEnMain maisonReference = modelMaison == null ? resolveMaison(request.getMaisonCleEnMainId()) : null;
        if (maisonReference != null
                && !Objects.equals(maisonReference.getSocietePromotrice().getId(), societe.getId())) {
            throw new BadRequestException("Cette maison clé en main n'est pas proposée par la société choisie");
        }
        boolean personnalise = modelMaison == null && maisonReference == null;
        if (modelMaison != null) {
            if (modelMaison.getSocietePromotrice() != null
                    && !Objects.equals(modelMaison.getSocietePromotrice().getId(), societe.getId())) {
                throw new BadRequestException("Ce modèle n'appartient pas au catalogue de la société choisie");
            }
            verifierCompatibilite(modelMaison, request.getSuperficieTerrain(), clean(request.getTypeTerrain()));
        } else if (personnalise && clean(request.getDescriptionModelePersonnalise()) == null) {
            throw new BadRequestException("Choisissez un modèle, une maison clé en main, ou décrivez la maison souhaitée");
        }

        ProjetConstruction projet = ProjetConstruction.builder()
                .numeroDossier(genererNumeroDossier())
                .numeroTitreFoncier(request.getNumeroTitreFoncier().trim())
                .localisationTerrain(clean(request.getLocalisationTerrain()))
                .superficieTerrain(request.getSuperficieTerrain())
                .typeTerrain(clean(request.getTypeTerrain()))
                .description(clean(request.getDescription()))
                .budgetEstime(request.getBudgetEstime())
                .descriptionModelePersonnalise(personnalise ? clean(request.getDescriptionModelePersonnalise()) : null)
                .nombreChambresSouhaitees(personnalise ? request.getNombreChambresSouhaitees() : null)
                .nombreSallesBainSouhaitees(personnalise ? request.getNombreSallesBainSouhaitees() : null)
                .surfaceConstruiteSouhaitee(personnalise ? request.getSurfaceConstruiteSouhaitee() : null)
                .maisonReference(maisonReference)
                .statut(StatutProjet.EN_ETUDE)
                .progression(0)
                .etapeAvancement("Dossier reçu")
                .dateDemande(LocalDateTime.now())
                .acquereur(acquereur)
                .modelMaison(modelMaison)
                .societePromotrice(societe)
                .build();

        return mapToResponse(projetConstructionRepository.save(projet));
    }

    private String genererNumeroDossier() {
        String numero;
        do {
            numero = "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (projetConstructionRepository.existsByNumeroDossier(numero));
        return numero;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetConstructionResponse getById(Long id) {
        ProjetConstruction projet = findEntityById(id);
        acces.verifierLecture(projet);
        return mapToResponse(projet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetConstructionResponse> getAll() {
        return projetConstructionRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public ProjetConstructionResponse update(Long id, ProjetConstructionRequest request) {
        ProjetConstruction existing = findEntityById(id);
        acces.verifierGestion(existing);

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
        acces.verifierGestion(existing);
        verifierEnEtude(existing);
        existing.validerProjet();
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse refuser(Long id, String motifRefus) {
        ProjetConstruction existing = findEntityById(id);
        acces.verifierGestion(existing);
        verifierEnEtude(existing);
        if (clean(motifRefus) == null) throw new BadRequestException("Indiquez le motif du refus à l'acquéreur");
        existing.refuserProjet(motifRefus);
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse changerModele(Long id, Long modelMaisonId) {
        ProjetConstruction existing = findEntityById(id);
        acces.verifierGestion(existing);
        ModelMaison model = resolveModel(modelMaisonId);
        if (model == null) throw new BadRequestException("Le modèle de maison est obligatoire");
        verifierCompatibilite(model, existing.getSuperficieTerrain(), existing.getTypeTerrain());
        existing.setModelMaison(model);
        existing.setDateDerniereMiseAJour(LocalDateTime.now());
        return mapToResponse(projetConstructionRepository.save(existing));
    }

    @Override
    public ProjetConstructionResponse mettreAJourAvancement(Long id, AvancementProjetRequest request) {
        ProjetConstruction existing = findEntityById(id);
        acces.verifierGestion(existing);
        if (existing.getStatut() != StatutProjet.ACCEPTER) {
            throw new BadRequestException("L'avancement ne se suit qu'une fois le projet accepté");
        }
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

    private void verifierEnEtude(ProjetConstruction projet) {
        if (projet.getStatut() != StatutProjet.EN_ETUDE) {
            throw new BadRequestException("Ce projet a déjà été traité");
        }
    }

    private void verifierCompatibilite(ModelMaison model, Double superficie, String typeTerrain) {
        if (superficie != null && model.getSurfaceTerrainMin() != null && superficie < model.getSurfaceTerrainMin()) {
            throw new BadRequestException("Ce modèle nécessite au minimum " + model.getSurfaceTerrainMin() + " m² de terrain");
        }
        if (superficie != null && model.getSurfaceTerrainMax() != null && superficie > model.getSurfaceTerrainMax()) {
            throw new BadRequestException("Ce modèle est prévu pour un terrain de " + model.getSurfaceTerrainMax() + " m² maximum");
        }
        if (model.getTypeTerrainCompatible() != null && typeTerrain != null
                && !model.getTypeTerrainCompatible().equalsIgnoreCase(typeTerrain)) {
            throw new BadRequestException("Le modèle sélectionné n'est pas compatible avec le type de terrain renseigné");
        }
    }

    private Acquereur resolveAcquereur(Long id) {
        return id == null ? null : acquereurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + id));
    }
    private ModelMaison resolveModel(Long id) {
        return id == null ? null : modelMaisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + id));
    }
    private MaisonCleEnMain resolveMaison(Long id) {
        return id == null ? null : maisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maison clé en main introuvable avec l'identifiant : " + id));
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
                .modelePersonnalise(e.isModelePersonnalise())
                .maisonReferenceId(e.getMaisonReference() != null ? e.getMaisonReference().getId() : null)
                .maisonReferenceTitre(e.getMaisonReference() != null ? e.getMaisonReference().getTitre() : null)
                .maisonReferenceImageUrl(e.getMaisonReference() != null ? couvertureResolver.pourBien(e.getMaisonReference().getId()) : null)
                .descriptionModelePersonnalise(e.getDescriptionModelePersonnalise())
                .nombreChambresSouhaitees(e.getNombreChambresSouhaitees())
                .nombreSallesBainSouhaitees(e.getNombreSallesBainSouhaitees())
                .surfaceConstruiteSouhaitee(e.getSurfaceConstruiteSouhaitee())
                .fichiers(e.getFichiers().stream().map(FichierProjetServiceImpl::versReponse).toList())
                .build();
    }
}
