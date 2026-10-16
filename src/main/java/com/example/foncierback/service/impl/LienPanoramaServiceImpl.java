package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.LienPanoramaRequest;
import com.example.foncierback.dto.response.LienPanoramaResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.LienPanorama;
import com.example.foncierback.entity.MediaFoncier;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.TypeMedia;
import com.example.foncierback.repository.LienPanoramaRepository;
import com.example.foncierback.repository.MediaFoncierRepository;
import com.example.foncierback.service.LienPanoramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Points de passage entre les panoramas 360° d'une même maison clé en main.
 * Seul le responsable de la société de la maison (ou l'administrateur) les modifie.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LienPanoramaServiceImpl implements LienPanoramaService {

    private static final String TYPE_MAISON = "MAISON_CLE_EN_MAIN";
    /** Position par défaut du point retour, devant soi en entrant : à déplacer ensuite. */
    private static final double ELEVATION_PAR_DEFAUT = -10;

    private final LienPanoramaRepository lienRepository;
    private final MediaFoncierRepository mediaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LienPanoramaResponse> getByBien(Long bienId) {
        return lienRepository.findBySourceBienFoncierIdOrderByIdAsc(bienId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<LienPanoramaResponse> creer(LienPanoramaRequest request) {
        if (request.getSourceId() == null || request.getCibleId() == null) {
            throw new BadRequestException("Indiquez le panorama de départ et celui d'arrivée.");
        }
        MediaFoncier source = panorama(request.getSourceId());
        MediaFoncier cible = panorama(request.getCibleId());
        if (Objects.equals(source.getId(), cible.getId())) {
            throw new BadRequestException("Un point de passage doit mener vers une autre pièce.");
        }
        BienFoncier bien = source.getBienFoncier();
        if (bien == null || cible.getBienFoncier() == null || !Objects.equals(bien.getId(), cible.getBienFoncier().getId())) {
            throw new BadRequestException("Les deux panoramas doivent appartenir à la même maison.");
        }
        if (!TYPE_MAISON.equals(bien.typeBien())) {
            throw new BadRequestException("La visite à panoramas liés est réservée aux maisons clés en main.");
        }
        verifierGestion(bien);

        List<LienPanorama> crees = new ArrayList<>();
        crees.add(lienRepository.save(LienPanorama.builder()
                .source(source)
                .cible(cible)
                .azimut(request.getAzimut())
                .elevation(request.getElevation())
                .libelle(libelle(request.getLibelle(), cible))
                .build()));

        if (Boolean.TRUE.equals(request.getCreerRetour()) && !lienRepository.existsBySourceIdAndCibleId(cible.getId(), source.getId())) {
            crees.add(lienRepository.save(LienPanorama.builder()
                    .source(cible)
                    .cible(source)
                    // En face de l'entrée : on se retourne pour repartir d'où l'on vient.
                    .azimut(normaliser(request.getAzimut() + 180))
                    .elevation(ELEVATION_PAR_DEFAUT)
                    .libelle(libelle(null, source))
                    .build()));
        }
        return crees.stream().map(this::mapToResponse).toList();
    }

    @Override
    public LienPanoramaResponse modifier(Long id, LienPanoramaRequest request) {
        LienPanorama lien = findLien(id);
        verifierGestion(lien.getSource().getBienFoncier());
        lien.setAzimut(request.getAzimut());
        lien.setElevation(request.getElevation());
        if (request.getLibelle() != null) {
            lien.setLibelle(libelle(request.getLibelle(), lien.getCible()));
        }
        return mapToResponse(lienRepository.save(lien));
    }

    @Override
    public void supprimer(Long id) {
        LienPanorama lien = findLien(id);
        verifierGestion(lien.getSource().getBienFoncier());
        lienRepository.delete(lien);
    }

    // ------------------------------------------------------------------ Outils

    private MediaFoncier panorama(Long id) {
        MediaFoncier media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panorama introuvable avec l'id : " + id));
        if (media.getType() != TypeMedia.PANORAMA_360) {
            throw new BadRequestException("Seuls les panoramas 360° peuvent être reliés.");
        }
        return media;
    }

    private LienPanorama findLien(Long id) {
        return lienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point de passage introuvable avec l'id : " + id));
    }

    private void verifierGestion(BienFoncier bien) {
        if (SecurityUtils.isAdmin()) return;
        SocietePromotrice societe = bien != null ? bien.societeGestionnaire() : null;
        boolean responsable = SecurityUtils.isResponsableSociete()
                && societe != null && Objects.equals(societe.getId(), SecurityUtils.getCurrentSocieteId());
        if (!responsable) {
            throw new AccessDeniedException("Seul le responsable de la société peut modifier la visite de cette maison.");
        }
    }

    /** Libellé saisi, sinon le titre du panorama d'arrivée (« Chambre »), sinon « Pièce suivante ». */
    private String libelle(String saisi, MediaFoncier cible) {
        if (saisi != null && !saisi.isBlank()) return saisi.trim();
        return cible.getTitre() != null && !cible.getTitre().isBlank() ? cible.getTitre().trim() : "Pièce suivante";
    }

    private double normaliser(double azimut) {
        double a = ((azimut + 180) % 360 + 360) % 360 - 180;
        return a == -180 ? 180 : a;
    }

    private LienPanoramaResponse mapToResponse(LienPanorama l) {
        return LienPanoramaResponse.builder()
                .id(l.getId())
                .sourceId(l.getSource().getId())
                .cibleId(l.getCible().getId())
                .azimut(l.getAzimut())
                .elevation(l.getElevation())
                .libelle(l.getLibelle())
                .build();
    }
}
