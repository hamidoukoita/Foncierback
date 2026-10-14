package com.example.foncierback.service;

import com.example.foncierback.entity.MediaFoncier;
import com.example.foncierback.entity.enums.TypeMedia;
import com.example.foncierback.repository.MediaFoncierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Photo de couverture d'une cité ou d'un bien : la première photo de sa galerie
 * (celle choisie via « Définir comme couverture »). Chemin relatif servi sous /uploads.
 * Fournit aussi le nombre de panoramas 360°, signalés sur les cartes des applications.
 */
@Component
@RequiredArgsConstructor
public class CouvertureResolver {

    public static final String PREFIXE_URL = "/uploads/medias/";

    private final MediaFoncierRepository mediaRepository;

    public String pourProgramme(Long programmeId) {
        return programmeId == null ? null : url(mediaRepository
                .findFirstByProgrammeFoncierIdAndTypeOrderByOrdreAscIdAsc(programmeId, TypeMedia.PHOTO)
                .orElse(null));
    }

    public String pourBien(Long bienId) {
        return bienId == null ? null : url(mediaRepository
                .findFirstByBienFoncierIdAndTypeOrderByOrdreAscIdAsc(bienId, TypeMedia.PHOTO)
                .orElse(null));
    }

    public long panoramasProgramme(Long programmeId) {
        return programmeId == null ? 0 : mediaRepository.countByProgrammeFoncierIdAndType(programmeId, TypeMedia.PANORAMA_360);
    }

    public long panoramasBien(Long bienId) {
        return bienId == null ? 0 : mediaRepository.countByBienFoncierIdAndType(bienId, TypeMedia.PANORAMA_360);
    }

    private String url(MediaFoncier media) {
        return media == null || media.getNomFichier() == null ? null : PREFIXE_URL + media.getNomFichier();
    }
}
