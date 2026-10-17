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
    /** Montant à payer, fixé par la société (zéro si elle a choisi la gratuité). */
    private BigDecimal montant;
    private boolean payante;
    /** Durée de la réservation une fois le décompte démarré par l'agent. */
    private int dureeHeures;
    private boolean reservationMultiple;
    /** Nombre maximal de réservations en cours sur ce bien, et nombre actuel. */
    private int reservationsMax;
    private long reservationsEnCours;
    private boolean dejaReserveParMoi;
    private boolean vendu;
    private boolean reservable;
    /** Raison pour laquelle le bien n'est pas réservable, null sinon. */
    private String motifIndisponibilite;
    private boolean visitePossible;
    private String motifVisiteImpossible;
    private String societeNom;
}
