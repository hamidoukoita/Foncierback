package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutParcelle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienFoncierResponse {

    private Long id;
    private String reference;
    private Double superficie;
    private BigDecimal prix;
    private Double facade;
    private Double profondeur;
    private Double latitude;
    private Double longitude;
    private StatutParcelle statut;
    /** Le bien accepte-t-il plusieurs réservations actives en parallèle ? */
    private Boolean reservationMultiple;
    private String typeBien; // "LOT_PROGRAMME" ou "PARCELLE_INDIVIDUELLE"

    @Builder.Default
    private List<CommoditerResponse> commodites = new ArrayList<>();

    // Champs spécifiques au LotProgramme
    private String numeroLot;
    private String numeroIlot;
    private String numeroIlotLotissement;
    private Long programmeId;
    private String programmeNom;

    // Champs spécifiques à la ParcelleIndividuelle
    private String numeroTitreFoncier;
    private Boolean murCloture;
    private Boolean eauSomapep;
    private Boolean electriciteEdm;
    private Boolean voieBitumee;
    private Long societeId;
    private String societeNom;
}
