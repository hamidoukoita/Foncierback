package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutParcelle;
import jakarta.validation.constraints.*;
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
public class MaisonCleEnMainRequest {

    @NotBlank(message = "Le titre de la maison est obligatoire")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    private String reference;

    @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être strictement positif")
    private BigDecimal prix;

    @NotNull(message = "La superficie du terrain est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    private Double superficie;

    @PositiveOrZero(message = "La surface habitable ne peut pas être négative")
    private Double surfaceHabitable;

    @Min(value = 0, message = "Le nombre de chambres ne peut pas être négatif")
    private Integer nombreChambres;

    @Min(value = 0, message = "Le nombre de salles de bain ne peut pas être négatif")
    private Integer nombreSallesBain;

    @Min(value = 0, message = "Le nombre de niveaux ne peut pas être négatif")
    private Integer nombreNiveaux;

    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    private String numeroTitreFoncier;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Le statut est obligatoire")
    private StatutParcelle statut;

    @Builder.Default
    private List<@Size(max = 80) String> equipements = new ArrayList<>();

    @Builder.Default
    private List<Long> commoditeIds = new ArrayList<>();

    /** Ignoré pour un responsable : la société est celle de la session. */
    private Long societeId;
}
