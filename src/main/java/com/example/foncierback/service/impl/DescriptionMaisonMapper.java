package com.example.foncierback.service.impl;

import com.example.foncierback.dto.DescriptionMaisonDto;
import com.example.foncierback.entity.DescriptionMaison;
import com.example.foncierback.entity.enums.EquipementMaison;

import java.util.EnumSet;
import java.util.Set;

/** Conversion entité ↔ DTO de la description d'une maison, partagée par les projets et les propositions. */
final class DescriptionMaisonMapper {

    private DescriptionMaisonMapper() {
    }

    static DescriptionMaison versEntite(DescriptionMaisonDto dto) {
        if (dto == null) return null;
        Set<EquipementMaison> equipements = EnumSet.noneOf(EquipementMaison.class);
        if (dto.getEquipements() != null) equipements.addAll(dto.getEquipements());
        Integer chambres = dto.getNombreChambres();
        Integer suites = dto.getNombreSuites();
        // Les chambres avec salle de bain sont comprises dans le nombre de chambres.
        if (chambres != null && suites != null && suites > chambres) suites = chambres;
        return DescriptionMaison.builder()
                .nombreNiveaux(dto.getNombreNiveaux())
                .style(dto.getStyle())
                .nombreChambres(chambres)
                .nombreSuites(suites)
                .nombreSallesBain(dto.getNombreSallesBain())
                .nombreSalons(dto.getNombreSalons())
                .salleAManger(dto.getSalleAManger())
                .toilettesVisiteurs(dto.getToilettesVisiteurs())
                .magasin(dto.getMagasin())
                .cuisine(dto.getCuisine())
                .placesGarage(dto.getPlacesGarage())
                .equipements(equipements)
                .surfaceHabitable(dto.getSurfaceHabitable())
                .precisions(nettoyer(dto.getPrecisions()))
                .build();
    }

    static DescriptionMaisonDto versDto(DescriptionMaison e) {
        if (e == null) return null;
        return DescriptionMaisonDto.builder()
                .nombreNiveaux(e.getNombreNiveaux())
                .style(e.getStyle())
                .nombreChambres(e.getNombreChambres())
                .nombreSuites(e.getNombreSuites())
                .nombreSallesBain(e.getNombreSallesBain())
                .nombreSalons(e.getNombreSalons())
                .salleAManger(e.getSalleAManger())
                .toilettesVisiteurs(e.getToilettesVisiteurs())
                .magasin(e.getMagasin())
                .cuisine(e.getCuisine())
                .placesGarage(e.getPlacesGarage())
                .equipements(e.getEquipements() == null ? Set.of() : e.getEquipements())
                .surfaceHabitable(e.getSurfaceHabitable())
                .precisions(e.getPrecisions())
                .build();
    }

    /** Une description sans niveau ni chambre ni précision n'apporte rien au promoteur. */
    static boolean estRenseignee(DescriptionMaisonDto dto) {
        return dto != null && (dto.getNombreNiveaux() != null || dto.getNombreChambres() != null
                || nettoyer(dto.getPrecisions()) != null);
    }

    private static String nettoyer(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }
}
