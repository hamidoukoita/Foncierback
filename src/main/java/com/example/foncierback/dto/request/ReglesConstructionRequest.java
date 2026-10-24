package com.example.foncierback.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Règles de la société pour « Construire sur mon terrain ». */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglesConstructionRequest {

    /** Budget minimum d'un projet, en FCFA ; absent ou zéro = pas de minimum. */
    @PositiveOrZero(message = "Le budget minimum ne peut pas être négatif")
    @DecimalMax(value = "100000000000", message = "Le budget minimum ne peut pas dépasser 100 milliards de FCFA")
    private BigDecimal budgetMinimumConstruction;
}
