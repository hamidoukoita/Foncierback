package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutProjet;
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
public class ProjetConstructionResponse {

    private Long id;
    private String numeroDossier;
    private String numeroTitreFoncier;
    private String localisationTerrain;
    private Double superficieTerrain;
    private String description;
    private BigDecimal budgetEstime;
    private String documentTfUrl;
    private StatutProjet statut;
    private String motifRefus;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private Long acquereurId;
    private Long modelMaisonId;
    private Long societeId;
    private Long agentId;
}
