package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelMaisonRequest {

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libeller;

    private String description;

    private String imageUrl;

    private String planUrl;

    @PositiveOrZero(message = "La surface minimale ne peut pas être négative")
    private Double surfaceTerrainMin;

    @PositiveOrZero(message = "La surface maximale ne peut pas être négative")
    private Double surfaceTerrainMax;

    @PositiveOrZero(message = "La surface construite ne peut pas être négative")
    private Double surfaceConstruite;

    @PositiveOrZero(message = "Le nombre de chambres ne peut pas être négatif")
    private Integer nombreChambres;

    @PositiveOrZero(message = "Le nombre de salles de bain ne peut pas être négatif")
    private Integer nombreSallesBain;

    @Size(max = 50, message = "Le type de terrain ne doit pas dépasser 50 caractères")
    private String typeTerrainCompatible;

    private Long societeId;
}
