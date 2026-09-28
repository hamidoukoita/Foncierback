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
public class ParcelleIndividuelleResponse {

    private Long id;
    private String reference;
    private String numeroTitreFoncier;
    private Double superficie;
    private BigDecimal prix;
    private Double facade;
    private Double profondeur;
    private Double latitude;
    private Double longitude;
    private StatutParcelle statut;
    private Boolean murCloture;
    private Boolean eauSomapep;
    private Boolean electriciteEdm;
    private Boolean voieBitumee;
    private Long societeId;
    private String societeNom;

    @Builder.Default
    private List<CommoditerResponse> commodites = new ArrayList<>();
}
