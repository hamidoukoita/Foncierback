package com.example.foncierback.service.impl;

import com.example.foncierback.service.ReservationService;
import com.example.foncierback.service.SuiteVenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Fait expirer les réservations dont le décompte (démarré par l'agent) est échu sans vente :
 * le montant payé n'est pas rendu et le bien redevient disponible.
 * Effectue aussi les remboursements (simulés) des réservations dont le bien a été vendu à un autre.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;
    private final SuiteVenteService suiteVenteService;

    @Scheduled(
            fixedDelayString = "${app.reservations.expiration-check-ms:60000}",
            initialDelayString = "${app.reservations.expiration-initial-delay-ms:10000}"
    )
    public void expirerReservationsEchues() {
        int nombre = reservationService.expirerReservationsEchues();
        if (nombre > 0) {
            log.info("{} réservation(s) expirée(s) : délai écoulé sans vente.", nombre);
        }
        int rembourses = suiteVenteService.traiterEcheances();
        if (rembourses > 0) {
            log.info("{} réservation(s) remboursée(s) après la vente du bien à un autre acquéreur.", rembourses);
        }
    }
}
