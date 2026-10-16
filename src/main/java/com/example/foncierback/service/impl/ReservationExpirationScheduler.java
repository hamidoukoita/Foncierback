package com.example.foncierback.service.impl;

import com.example.foncierback.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Fait expirer les réservations dont le décompte (démarré par l'agent) est échu sans vente :
 * le montant payé n'est pas rendu et le bien redevient disponible.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    @Scheduled(
            fixedDelayString = "${app.reservations.expiration-check-ms:60000}",
            initialDelayString = "${app.reservations.expiration-initial-delay-ms:10000}"
    )
    public void expirerReservationsEchues() {
        int nombre = reservationService.expirerReservationsEchues();
        if (nombre > 0) {
            log.info("{} réservation(s) expirée(s) : délai écoulé sans vente.", nombre);
        }
    }
}
