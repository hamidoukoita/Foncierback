package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelMaisonResponse {

    private Long id;
    private String libeller;
    private String description;
    private String imageUrl;
    private String planUrl;
    private Double surfaceTerrainMin;
    private Double surfaceTerrainMax;
    private Double surfaceConstruite;
    private Integer nombreChambres;
    private Integer nombreSallesBain;
    private String typeTerrainCompatible;
    private Long societeId;
    private String societeNom;
}
