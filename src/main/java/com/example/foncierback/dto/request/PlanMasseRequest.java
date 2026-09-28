package com.example.foncierback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanMasseRequest {

    @NotBlank(message = "Le plan est obligatoire")
    @Schema(description = "Contenu ou URL/SVG du plan de masse", example = "data:image/svg+xml;base64,...")
    private String plan;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    @Schema(description = "Description du plan de masse", example = "Plan de masse officiel validé")
    private String description;

    @Size(max = 50, message = "La version ne doit pas dépasser 50 caractères")
    @Schema(description = "Version du plan", example = "v1.0")
    private String version;

    @NotNull(message = "L'identifiant du programme foncier est obligatoire")
    @Schema(description = "Identifiant du programme foncier associé", example = "1")
    private Long programmeId;
}
