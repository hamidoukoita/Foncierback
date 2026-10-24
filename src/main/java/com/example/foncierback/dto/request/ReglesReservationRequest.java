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

    /** Montant fixe payé pour réserver un lot, une parcelle ou une maison clé en main (FCFA). */
    @NotNull(message = "Le prix de la réservation est obligatoire")
    @PositiveOrZero(message = "Le prix de la réservation ne peut pas être négatif")
    @DecimalMax(value = "5000000", message = "Le prix de la réservation ne peut pas dépasser 5 000 000 FCFA")
    private BigDecimal prixReservation;

    /** Nombre maximal de réservations en cours sur un même bien (1 = réservation unique). */
    @NotNull(message = "Indiquez combien de réservations un même bien peut recevoir")
    @Min(value = 1, message = "Un bien doit accepter au moins une réservation")
    @Max(value = ReglesReservationResolver.RESERVATIONS_MAX_LIMITE, message = "Un bien ne peut pas recevoir plus de 20 réservations en cours")
    private Integer reservationsMax;

    /** Part du montant rendue si l'acquéreur annule, de 0 (rien) à 100 % (tout) ; absente = inchangée. */
    @Min(value = 0, message = "Le pourcentage remboursé ne peut pas être négatif")
    @Max(value = 100, message = "Le pourcentage remboursé ne peut pas dépasser 100 %")
    private Integer pourcentageRemboursementAnnulation;
}
