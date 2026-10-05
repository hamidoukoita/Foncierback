package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.PlanElementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanMasseElementRequest {

    @NotNull(message = "Le type de l'élément est obligatoire")
    @Schema(example = "ECOLE")
    private PlanElementType type;

    @Size(max = 120)
    private String nom;

    @Size(max = 255)
    private String description;

    @NotBlank(message = "La géométrie est obligatoire")
    @Schema(description = "Géométrie JSON. Exemple : {\"type\":\"POINT\",\"x\":150,\"y\":200}")
    private String geometryJson;

    private String styleJson;

    private Boolean visible;

    private Integer zIndex;

    @NotNull(message = "L'identifiant du plan de masse est obligatoire")
    private Long planMasseId;
}
