package com.example.foncierback.service;

import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutReservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Règles de réservation fixées par chaque société pour l'ensemble de ses biens :
 * - délai d'attribution : heures dont elle dispose pour affecter un agent à une demande
 *   (passé ce délai, une demande sans agent est annulée) ;
 * - réservation multiple : un même lot ou une même parcelle peut-il être réservé par plusieurs acquéreurs.
 */
@Component
public class ReglesReservationResolver {

    public static final int DELAI_PAR_DEFAUT_HEURES = 72;
    public static final int DELAI_MIN_HEURES = 24;
    public static final int DELAI_MAX_HEURES = 336;

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

    public int heures(SocietePromotrice societe) {
        return societe != null && societe.getDelaiAttributionHeures() != null
                ? societe.getDelaiAttributionHeures()
                : DELAI_PAR_DEFAUT_HEURES;
    }

    public int heures(Reservation reservation) {
        return heures(societe(reservation.getBienFoncier()));
    }

    /** Date limite d'affectation d'un agent, selon le délai actuel de la société. */
    public LocalDateTime echeance(Reservation reservation) {
        return reservation.getDateReservation() == null
                ? null
                : reservation.getDateReservation().plusHours(heures(reservation));
    }

    /** Vrai pour une demande encore en attente, sans agent, dont l'échéance est atteinte. */
    public boolean estExpiree(Reservation reservation, LocalDateTime maintenant) {
        LocalDateTime echeance = echeance(reservation);
        return reservation.getStatut() == StatutReservation.EN_ATTENTE
                && reservation.getAgentPromoteur() == null
                && echeance != null
                && !echeance.isAfter(maintenant);
    }

    public String motifAnnulation(Reservation reservation) {
        return "Annulation automatique : aucun agent n'a été affecté dans les "
                + heures(reservation) + " heures suivant la demande.";
    }
}
