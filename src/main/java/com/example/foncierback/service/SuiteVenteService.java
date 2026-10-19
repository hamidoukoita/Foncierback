package com.example.foncierback.service;

import com.example.foncierback.dto.response.BienRemplacementResponse;
import com.example.foncierback.dto.response.ReservationResponse;

import java.util.List;

/**
 * Réservation payée dont le bien a été vendu à un autre acquéreur : l'acquéreur choisit
 * de reporter son paiement sur un autre bien de la société ou d'être remboursé sous 24 h.
 * Sans choix dans les 48 h, il est remboursé d'office.
 */
public interface SuiteVenteService {

    int DELAI_CHOIX_HEURES = 48;
    int DELAI_REMBOURSEMENT_HEURES = 24;

    /** Biens disponibles de la même société sur lesquels reporter la réservation. */
    List<BienRemplacementResponse> biensDeRemplacement(Long reservationId);

    ReservationResponse demanderRemboursement(Long reservationId);

    /** Reporte le paiement sur le bien choisi ; retourne la nouvelle réservation. */
    ReservationResponse remplacer(Long reservationId, Long bienId);

    /** Remboursements d'office (choix non fait) et remboursements échus ; retourne leur nombre. */
    int traiterEcheances();
}
