package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.MaisonCleEnMainRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.dto.response.MaisonCleEnMainResponse;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.MaisonCleEnMain;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.MaisonCleEnMainRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.CouvertureResolver;
import com.example.foncierback.service.MaisonCleEnMainService;
import com.example.foncierback.service.ReglesReservationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MaisonCleEnMainServiceImpl implements MaisonCleEnMainService {

    private final MaisonCleEnMainRepository maisonRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final SocietePromotriceRepository societeRepository;
    private final CommoditerRepository commoditerRepository;
    private final CouvertureResolver couvertureResolver;
    private final ReglesReservationResolver reglesResolver;

    @Override
    public MaisonCleEnMainResponse create(MaisonCleEnMainRequest request) {
        SocietePromotrice societe = societeDeLaRequete(request);
        if (societe.getStatutAgrement() != StatutAgrement.VERIFIER) {
            throw new BadRequestException("Votre agrément doit être validé avant de publier une maison.");
        }
        String reference = nettoyer(request.getReference());
        if (reference == null) {
            reference = "MCM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (bienFoncierRepository.existsByReference(reference)) {
            throw new BadRequestException("Un bien avec la référence '" + reference + "' existe déjà");
        }

        MaisonCleEnMain maison = MaisonCleEnMain.builder()
                .reference(reference)
                .societePromotrice(societe)
                .build();
        appliquer(maison, request);
        return mapToResponse(maisonRepository.save(maison));
    }

    @Override
    public MaisonCleEnMainResponse update(Long id, MaisonCleEnMainRequest request) {
        MaisonCleEnMain maison = findEntityById(id);
        verifierGestion(maison);
        String reference = nettoyer(request.getReference());
        if (reference != null && !reference.equalsIgnoreCase(maison.getReference())) {
            if (bienFoncierRepository.existsByReferenceAndIdNot(reference, id)) {
                throw new BadRequestException("Un bien avec la référence '" + reference + "' existe déjà");
            }
            maison.setReference(reference);
        }
        appliquer(maison, request);
        return mapToResponse(maisonRepository.save(maison));
    }

    @Override
    public void delete(Long id) {
        MaisonCleEnMain maison = findEntityById(id);
        verifierGestion(maison);
        maisonRepository.delete(maison);
    }

    @Override
    @Transactional(readOnly = true)
    public MaisonCleEnMainResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaisonCleEnMainResponse> getCatalogue() {
        return maisonRepository.findBySocietePromotriceStatutAgrementOrderByIdDesc(StatutAgrement.VERIFIER)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaisonCleEnMainResponse> getBySociete(Long societeId) {
        return maisonRepository.findBySocietePromotriceIdOrderByIdDesc(societeId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaisonCleEnMain findEntityById(Long id) {
        return maisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maison clé en main introuvable avec l'identifiant : " + id));
    }

    /** Un responsable publie toujours pour sa propre société ; l'administration choisit la société. */
    private SocietePromotrice societeDeLaRequete(MaisonCleEnMainRequest request) {
        Long societeId = SecurityUtils.isAdmin() ? request.getSocieteId() : SecurityUtils.getCurrentSocieteId();
        if (societeId == null) throw new BadRequestException("La société promotrice est obligatoire");
        return societeRepository.findById(societeId)
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId));
    }

    private void verifierGestion(MaisonCleEnMain maison) {
        if (SecurityUtils.isAdmin()) return;
        Long societe = maison.getSocietePromotrice() != null ? maison.getSocietePromotrice().getId() : null;
        if (!Objects.equals(societe, SecurityUtils.getCurrentSocieteId())) {
            throw new AccessDeniedException("Cette maison n'appartient pas à votre société");
        }
    }

    private void appliquer(MaisonCleEnMain maison, MaisonCleEnMainRequest r) {
        maison.setTitre(r.getTitre().trim());
        maison.setDescription(nettoyer(r.getDescription()));
        maison.setPrix(r.getPrix());
        maison.setSuperficie(r.getSuperficie());
        maison.setSurfaceHabitable(r.getSurfaceHabitable());
        maison.setNombreChambres(r.getNombreChambres());
        maison.setNombreSallesBain(r.getNombreSallesBain());
        maison.setNombreNiveaux(r.getNombreNiveaux());
        maison.setNumeroTitreFoncier(nettoyer(r.getNumeroTitreFoncier()));
        maison.setAdresse(nettoyer(r.getAdresse()));
        maison.setStatut(r.getStatut());
        // Position conservée si non transmise (comme pour les parcelles).
        if (r.getLatitude() != null) maison.setLatitude(r.getLatitude());
        if (r.getLongitude() != null) maison.setLongitude(r.getLongitude());
        if (r.getEquipements() != null) {
            maison.getEquipements().clear();
            r.getEquipements().stream().map(this::nettoyer).filter(Objects::nonNull).distinct()
                    .forEach(maison.getEquipements()::add);
        }
        if (r.getCommoditeIds() != null) {
            List<Commoditer> commodites = commoditerRepository.findAllById(r.getCommoditeIds());
            maison.setCommodites(commodites);
        }
    }

    private String nettoyer(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private MaisonCleEnMainResponse mapToResponse(MaisonCleEnMain m) {
        SocietePromotrice societe = m.getSocietePromotrice();
        return MaisonCleEnMainResponse.builder()
                .id(m.getId())
                .reference(m.getReference())
                .titre(m.getTitre())
                .description(m.getDescription())
                .prix(m.getPrix())
                .superficie(m.getSuperficie())
                .surfaceHabitable(m.getSurfaceHabitable())
                .nombreChambres(m.getNombreChambres())
                .nombreSallesBain(m.getNombreSallesBain())
                .nombreNiveaux(m.getNombreNiveaux())
                .numeroTitreFoncier(m.getNumeroTitreFoncier())
                .adresse(m.getAdresse())
                .latitude(m.getLatitude())
                .longitude(m.getLongitude())
                .statut(m.getStatut())
                .reservationMultiple(reglesResolver.autoriseReservationMultiple(m))
                .imageUrl(couvertureResolver.pourBien(m.getId()))
                .nombrePanoramas(couvertureResolver.panoramasBien(m.getId()))
                .societeId(societe != null ? societe.getId() : null)
                .societeNom(societe != null ? societe.getNom() : null)
                .societeLogoUrl(societe != null ? societe.getLogoUrl() : null)
                .equipements(List.copyOf(m.getEquipements()))
                .commodites(m.getCommodites().stream().map(this::versCommodite).toList())
                .build();
    }

    private CommoditerResponse versCommodite(Commoditer c) {
        return CommoditerResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .icone(c.getIcone())
                .description(c.getDescription())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .typeCommoditeId(c.getTypeCommodite() != null ? c.getTypeCommodite().getId() : null)
                .typeCommoditeLibelle(c.getTypeCommodite() != null ? c.getTypeCommodite().getLibelle() : null)
                .build();
    }
}
