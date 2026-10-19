package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Bien disponible de la même société, proposé pour reporter une réservation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienRemplacementResponse {
    private Long id;
    /** LOT_PROGRAMME, PARCELLE_INDIVIDUELLE ou MAISON_CLE_EN_MAIN. */
    private String typeBien;
    private String designation;
    private String reference;
    private String programmeNom;
    private Double superficie;
    private BigDecimal prix;
    private String imageUrl;
}
