package com.example.foncierback.dto.request;

import com.example.foncierback.service.ReglesReservationResolver;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglesReservationRequest {

    /** Durée de la réservation, comptée à partir du démarrage du décompte par l'agent. */
    @NotNull(message = "La durée de réservation est obligatoire")
    @Min(value = ReglesReservationResolver.DELAI_MIN_HEURES, message = "La durée de réservation doit être d'au moins 24 heures")
    @Max(value = ReglesReservationResolver.DELAI_MAX_HEURES, message = "La durée de réservation ne peut pas dépasser 14 jours")
    private Integer delaiAttributionHeures;

    /** Montant fixe payé pour réserver un lot ou une parcelle (FCFA). */
    @NotNull(message = "Le prix de la réservation est obligatoire")
    @PositiveOrZero(message = "Le prix de la réservation ne peut pas être négatif")
    @DecimalMax(value = "5000000", message = "Le prix de la réservation ne peut pas dépasser 5 000 000 FCFA")
    private BigDecimal prixReservation;

    @NotNull(message = "Indiquez si un bien peut être réservé plusieurs fois")
    private Boolean reservationMultiple;
}
