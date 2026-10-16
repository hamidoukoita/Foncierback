package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Ce que l'acquéreur doit savoir avant de réserver un bien : montant, durée, disponibilité. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionsReservationResponse {
    private Long bienId;
    /** Montant à payer ; zéro pour une réservation gratuite (maison clé en main). */
    private BigDecimal montant;
    private boolean payante;
    /** Durée de la réservation une fois le décompte démarré par l'agent. */
    private int dureeHeures;
    private boolean reservationMultiple;
    private boolean reservable;
    /** Raison pour laquelle le bien n'est pas réservable, null sinon. */
    private String motifIndisponibilite;
    private String societeNom;
}
