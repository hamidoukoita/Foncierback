package com.example.foncierback.service;

import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Règles de réservation fixées par chaque société pour l'ensemble de ses biens :
 * - prix de la réservation : montant fixe payé par l'acquéreur pour réserver l'un de ses biens ;
 * - durée de la réservation : heures pendant lesquelles le bien reste bloqué une fois le décompte
 *   démarré par l'agent (passé ce délai sans vente, la réservation expire et le bien est libéré) ;
 * - nombre maximal de réservations en cours sur un même bien (1 = réservation unique) ;
 * - part du montant rendue quand l'acquéreur annule lui-même sa réservation (0 à 100 %).
 */
@Component
public class ReglesReservationResolver {

    public static final int DELAI_PAR_DEFAUT_HEURES = 72;
    public static final int DELAI_MIN_HEURES = 24;
    public static final int DELAI_MAX_HEURES = 336;

    public static final BigDecimal PRIX_PAR_DEFAUT = BigDecimal.valueOf(25_000);
    public static final long PRIX_MAX = 5_000_000;

    public static final int RESERVATIONS_MAX_LIMITE = 20;

    /** Sans réglage de la société, une annulation par l'acquéreur n'est pas remboursée. */
    public static final int REMBOURSEMENT_PAR_DEFAUT = 0;
    /** Plafond appliqué aux sociétés passées en « réservation multiple » avant le choix d'un nombre. */
    private static final int RESERVATIONS_MAX_HISTORIQUE = 3;

    /** Société qui gère le bien (lot, parcelle ou maison clé en main). */
    public SocietePromotrice societe(BienFoncier bien) {
        return bien != null ? bien.societeGestionnaire() : null;
    }

    /** Nombre maximal de réservations en cours sur un même bien de la société. */
    public int reservationsMax(SocietePromotrice societe) {
        if (societe == null) return 1;
        if (societe.getReservationsMaxParBien() != null) return Math.max(1, societe.getReservationsMaxParBien());
        return Boolean.TRUE.equals(societe.getReservationMultiple()) ? RESERVATIONS_MAX_HISTORIQUE : 1;
    }

    public int reservationsMax(BienFoncier bien) {
        return reservationsMax(societe(bien));
    }

    public boolean autoriseReservationMultiple(SocietePromotrice societe) {
        return reservationsMax(societe) > 1;
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

    /** Pourcentage du montant rendu à l'acquéreur qui annule sa réservation. */
    public int pourcentageRemboursement(SocietePromotrice societe) {
        if (societe == null || societe.getPourcentageRemboursementAnnulation() == null) return REMBOURSEMENT_PAR_DEFAUT;
        return Math.max(0, Math.min(100, societe.getPourcentageRemboursementAnnulation()));
    }

    public int pourcentageRemboursement(BienFoncier bien) {
        return pourcentageRemboursement(societe(bien));
    }

    /** Montant à payer pour réserver ce bien (lot, parcelle ou maison clé en main) : le prix de sa société. */
    public BigDecimal prix(BienFoncier bien) {
        return prix(societe(bien));
    }
}
