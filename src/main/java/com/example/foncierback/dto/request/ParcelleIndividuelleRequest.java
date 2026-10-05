package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutParcelle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class ParcelleIndividuelleRequest {

    @NotBlank(message = "Le numéro de Titre Foncier est obligatoire")
    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    private String numeroTitreFoncier;

    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    private String reference;

    @NotNull(message = "La superficie est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    private Double superficie;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être strictement positif")
    private BigDecimal prix;

    @Positive(message = "La façade doit être strictement positive")
    private Double facade;

    @Positive(message = "La profondeur doit être strictement positive")
    private Double profondeur;

    private Double latitude;

    private Double longitude;

    /** Géométrie vectorielle JSON de la parcelle. */
    private String geometryJson;

    @NotNull(message = "Le statut est obligatoire")
    private StatutParcelle statut;

    @Builder.Default
    private Boolean murCloture = false;

    @Builder.Default
    private Boolean eauSomapep = false;

    @Builder.Default
    private Boolean electriciteEdm = false;

    @Builder.Default
    private Boolean voieBitumee = false;

    private Long societeId;

    @Builder.Default
    private List<Long> commoditeIds = new ArrayList<>();
}
