package com.example.foncierback.service.impl;

import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.ReglesReservationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Annule automatiquement les demandes qui restent en attente sans agent au-delà
 * du délai d'attribution fixé par leur société. Les demandes déjà attribuées ne sont pas concernées.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReglesReservationResolver reglesResolver;

    @Scheduled(
            fixedDelayString = "${app.reservations.expiration-check-ms:60000}",
            initialDelayString = "${app.reservations.expiration-initial-delay-ms:10000}"
    )
    @Transactional
    public void annulerDemandesSansAgentExpirees() {
        LocalDateTime maintenant = LocalDateTime.now();
        // Candidates : plus anciennes que le plus court délai possible ; chaque société a ensuite le sien.
        List<Reservation> expirees = reservationRepository
                .findByStatutAndAgentPromoteurIsNullAndDateReservationLessThanEqual(
                        StatutReservation.EN_ATTENTE, maintenant.minusHours(ReglesReservationResolver.DELAI_MIN_HEURES))
                .stream()
                .filter(reservation -> reglesResolver.estExpiree(reservation, maintenant))
                .toList();

        if (expirees.isEmpty()) {
            return;
        }

        expirees.forEach(reservation -> reservation.annulerAutomatiquement(reglesResolver.motifAnnulation(reservation)));
        reservationRepository.saveAll(expirees);
        log.info("{} demande(s) de réservation annulée(s) automatiquement (délai d'attribution dépassé).", expirees.size());
    }
}
