package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutProjet;
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

    private String description;

    private BigDecimal budgetEstime;

    private String documentTfUrl;

    private StatutProjet statut;

    private String motifRefus;

    private LocalDateTime dateDemande;

    private Long acquereurId;

    private Long modelMaisonId;

    private Long societeId;

    private Long agentId;
}
