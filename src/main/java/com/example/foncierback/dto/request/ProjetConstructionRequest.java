package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutProjet;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetConstructionRequest {

    @Size(max = 100, message = "Le numéro de dossier ne doit pas dépasser 100 caractères")
    private String numeroDossier;

    @NotBlank(message = "Le numéro du Titre Foncier est obligatoire")
    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    private String numeroTitreFoncier;

    @Size(max = 200, message = "La localisation ne doit pas dépasser 200 caractères")
    private String localisationTerrain;

    @NotNull(message = "La superficie du terrain est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    private Double superficieTerrain;

    @Size(max = 50, message = "Le type de terrain ne doit pas dépasser 50 caractères")
    private String typeTerrain;

    private String description;

    private BigDecimal budgetEstime;

    private String documentTfUrl;

    private StatutProjet statut;

    @Min(value = 0, message = "La progression ne peut pas être négative")
    @Max(value = 100, message = "La progression ne peut pas dépasser 100")
    private Integer progression;

    @Size(max = 100, message = "L'étape ne doit pas dépasser 100 caractères")
    private String etapeAvancement;

    private String commentaireAvancement;

    private String motifRefus;

    private LocalDateTime dateDemande;

    private Long acquereurId;

    private Long modelMaisonId;

    private Long societeId;

    private Long agentId;
}
