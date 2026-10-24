package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import com.example.foncierback.dto.DescriptionMaisonDto;
import com.example.foncierback.entity.enums.StatutProjet;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Jackson passe par le constructeur vide puis par les setters, qui normalisent le numéro de Titre Foncier. */
@Data
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@AllArgsConstructor
@Builder
public class ProjetConstructionRequest {

    @Size(max = 100, message = "Le numéro de dossier ne doit pas dépasser 100 caractères")
    private String numeroDossier;

    @NotBlank(message = "Le numéro du Titre Foncier est obligatoire")
    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    @Pattern(regexp = Formats.TITRE_FONCIER, message = Formats.MESSAGE_TITRE_FONCIER)
    private String numeroTitreFoncier;

    @Size(max = 200, message = "La localisation ne doit pas dépasser 200 caractères")
    private String localisationTerrain;

    @NotNull(message = "La superficie du terrain est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    private Double superficieTerrain;

    @Size(max = 50, message = "Le type de terrain ne doit pas dépasser 50 caractères")
    private String typeTerrain;

    private String description;

    /** Estimation facultative de l'acquéreur, au moins égale au budget minimum de la société. */
    @Positive(message = "Le budget doit être strictement positif")
    private BigDecimal budgetEstime;

    /** Modèle personnalisé : questionnaire à renseigner quand aucun modèle ni maison de référence n'est choisi. */
    @Valid
    private DescriptionMaisonDto maisonSouhaitee;

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

    /** Maison clé en main de la société prise comme référence (alternative au modèle du catalogue). */
    private Long maisonCleEnMainId;

    private Long societeId;

    private Long agentId;

    public void setNumeroTitreFoncier(String valeur) {
        this.numeroTitreFoncier = Formats.titreFoncier(valeur);
    }
}
