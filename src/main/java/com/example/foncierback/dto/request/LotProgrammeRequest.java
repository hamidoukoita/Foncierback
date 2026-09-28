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
public class LotProgrammeRequest {

    @NotBlank(message = "Le numéro de lot est obligatoire")
    @Size(max = 50, message = "Le numéro de lot ne doit pas dépasser 50 caractères")
    private String numeroLot;

    @NotBlank(message = "Le numéro d'îlot est obligatoire")
    @Size(max = 50, message = "Le numéro d'îlot ne doit pas dépasser 50 caractères")
    private String numeroIlot;

    @Size(max = 100, message = "Le numéro d'îlot de lotissement ne doit pas dépasser 100 caractères")
    private String numeroIlotLotissement;

    @NotNull(message = "L'identifiant du programme foncier est obligatoire")
    private Long programmeId;

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

    @NotNull(message = "Le statut est obligatoire")
    private StatutParcelle statut;

    @Builder.Default
    private List<Long> commoditeIds = new ArrayList<>();
}
