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
public class LotProgrammeResponse {

    private Long id;
    private String reference;
    private Double superficie;
    private BigDecimal prix;
    private Double facade;
    private Double profondeur;
    private Double latitude;
    private Double longitude;

    /** Géométrie vectorielle JSON du lot dans le plan de masse. */
    private String geometryJson;
    private StatutParcelle statut;
    /** Le bien accepte-t-il plusieurs réservations actives en parallèle ? */
    private Boolean reservationMultiple;
    private String numeroLot;
    private String numeroIlot;
    private String numeroIlotLotissement;
    private Long programmeId;
    private String programmeNom;

    @Builder.Default
    private List<CommoditerResponse> commodites = new ArrayList<>();
}
