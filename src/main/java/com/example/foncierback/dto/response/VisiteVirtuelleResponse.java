package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Visite virtuelle d'une maison clé en main : pièce de départ et points de passage. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisiteVirtuelleResponse {
    /** Panorama par lequel commence la visite ; null : le premier de la galerie. */
    private Long panoramaDepartId;
    private List<LienPanoramaResponse> liens;
}
