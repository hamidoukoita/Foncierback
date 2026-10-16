package com.example.foncierback.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Création (source, cible obligatoires) ou déplacement / renommage d'un point de passage. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LienPanoramaRequest {

    private Long sourceId;

    private Long cibleId;

    @NotNull(message = "L'azimut du point est obligatoire")
    @DecimalMin(value = "-180", message = "L'azimut doit être compris entre -180° et 180°")
    @DecimalMax(value = "180", message = "L'azimut doit être compris entre -180° et 180°")
    private Double azimut;

    @NotNull(message = "L'élévation du point est obligatoire")
    @DecimalMin(value = "-90", message = "L'élévation doit être comprise entre -90° et 90°")
    @DecimalMax(value = "90", message = "L'élévation doit être comprise entre -90° et 90°")
    private Double elevation;

    @Size(max = 60, message = "Le libellé ne doit pas dépasser 60 caractères")
    private String libelle;

    /** À la création : ajoute aussi le point retour (cible → source), à placer ensuite. */
    private Boolean creerRetour;
}
