package com.example.foncierback.service;

import com.example.foncierback.dto.request.LienPanoramaRequest;
import com.example.foncierback.dto.response.LienPanoramaResponse;

import java.util.List;

/** Visite virtuelle d'une maison clé en main : points de passage entre ses panoramas 360°. */
public interface LienPanoramaService {

    List<LienPanoramaResponse> getByBien(Long bienId);

    /** Crée le point (et, si demandé, le point retour) ; retourne les points créés. */
    List<LienPanoramaResponse> creer(LienPanoramaRequest request);

    LienPanoramaResponse modifier(Long id, LienPanoramaRequest request);

    void supprimer(Long id);
}
