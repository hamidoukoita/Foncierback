package com.example.foncierback.dto.request;

import com.example.foncierback.dto.DescriptionMaisonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Proposition de la société : un modèle de son catalogue et/ou une maison décrite. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionProjetRequest {

    @NotBlank(message = "Donnez un titre à la proposition")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    private Long modelMaisonId;

    @Valid
    private DescriptionMaisonDto maison;

    @Positive(message = "Le prix estimé doit être strictement positif")
    private BigDecimal prixEstime;

    @Min(value = 1, message = "Le délai doit être d'au moins 1 mois")
    @Max(value = 120, message = "Le délai semble incorrect")
    private Integer delaiMois;

    @Size(max = 3000, message = "Le commentaire ne doit pas dépasser 3000 caractères")
    private String commentaire;
}
