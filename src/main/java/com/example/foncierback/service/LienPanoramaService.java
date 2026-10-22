package com.example.foncierback.service;

import com.example.foncierback.dto.request.LienPanoramaRequest;
import com.example.foncierback.dto.request.VueArriveeRequest;
import com.example.foncierback.dto.response.LienPanoramaResponse;
import com.example.foncierback.dto.response.VisiteVirtuelleResponse;

import java.util.List;

/** Visite virtuelle d'une maison clé en main : points de passage entre ses panoramas 360°. */
public interface LienPanoramaService {

    /** Pièce de départ et points de passage de la maison. */
    VisiteVirtuelleResponse getVisite(Long bienId);

    /** La visite commencera par ce panorama de la maison. */
    VisiteVirtuelleResponse definirDepart(Long bienId, Long panoramaId);

    /** Direction regardée en arrivant par ce point (les deux valeurs à null : calcul automatique). */
    LienPanoramaResponse definirVueArrivee(Long id, VueArriveeRequest request);

    /** Crée le point (et, si demandé, le point retour) ; retourne les points créés. */
    List<LienPanoramaResponse> creer(LienPanoramaRequest request);

    LienPanoramaResponse modifier(Long id, LienPanoramaRequest request);

    void supprimer(Long id);
}
