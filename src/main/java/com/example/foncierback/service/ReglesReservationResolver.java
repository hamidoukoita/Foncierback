package com.example.foncierback.service;

import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Règles de réservation fixées par chaque société pour l'ensemble de ses biens :
 * - prix de la réservation : montant fixe payé par l'acquéreur pour réserver un lot ou une parcelle ;
 * - durée de la réservation : heures pendant lesquelles le bien reste bloqué une fois le décompte
 *   démarré par l'agent (passé ce délai sans vente, la réservation expire et le bien est libéré) ;
 * - réservation multiple : un même bien peut-il être réservé par plusieurs acquéreurs à la fois.
 */
@Component
public class ReglesReservationResolver {

    public static final int DELAI_PAR_DEFAUT_HEURES = 72;
    public static final int DELAI_MIN_HEURES = 24;
    public static final int DELAI_MAX_HEURES = 336;

    public static final BigDecimal PRIX_PAR_DEFAUT = BigDecimal.valueOf(25_000);
    public static final long PRIX_MAX = 5_000_000;

    /** Société qui gère le bien (lot, parcelle ou maison clé en main). */
    public SocietePromotrice societe(BienFoncier bien) {
        return bien != null ? bien.societeGestionnaire() : null;
    }

    public boolean autoriseReservationMultiple(SocietePromotrice societe) {
        return societe != null && Boolean.TRUE.equals(societe.getReservationMultiple());
    }

    public boolean autoriseReservationMultiple(BienFoncier bien) {
        return autoriseReservationMultiple(societe(bien));
    }

    /** Durée de la réservation une fois le décompte démarré. */
    public int heures(SocietePromotrice societe) {
        return societe != null && societe.getDelaiAttributionHeures() != null
                ? societe.getDelaiAttributionHeures()
                : DELAI_PAR_DEFAUT_HEURES;
    }

    public int heures(Reservation reservation) {
        return heures(societe(reservation.getBienFoncier()));
    }

    public BigDecimal prix(SocietePromotrice societe) {
        return societe != null && societe.getPrixReservation() != null ? societe.getPrixReservation() : PRIX_PAR_DEFAUT;
    }

    /** Montant à payer pour réserver ce bien ; zéro si sa réservation est gratuite. */
    public BigDecimal prix(BienFoncier bien) {
        return bien != null && bien.reservationPayante() ? prix(societe(bien)) : BigDecimal.ZERO;
    }
}
