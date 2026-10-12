package com.example.foncierback.service.impl;

import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Annule automatiquement les demandes qui restent en attente sans agent
 * pendant 72 heures. Les demandes déjà attribuées ne sont pas annulées par ce délai.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private static final int DELAI_ATTRIBUTION_HEURES = 72;
    private static final String MOTIF_ANNULATION =
            "Annulation automatique : aucun agent n'a été affecté dans les 72 heures suivant la demande.";

    private final ReservationRepository reservationRepository;

    @Scheduled(
            fixedDelayString = "${app.reservations.expiration-check-ms:60000}",
            initialDelayString = "${app.reservations.expiration-initial-delay-ms:10000}"
    )
    @Transactional
    public void annulerDemandesSansAgentExpirees() {
        LocalDateTime limite = LocalDateTime.now().minusHours(DELAI_ATTRIBUTION_HEURES);
        List<Reservation> expirees = reservationRepository
                .findByStatutAndAgentPromoteurIsNullAndDateReservationLessThanEqual(
                        StatutReservation.EN_ATTENTE, limite);

        if (expirees.isEmpty()) {
            return;
        }

        LocalDateTime maintenant = LocalDateTime.now();
        expirees.forEach(reservation -> {
            reservation.setStatut(StatutReservation.ANNULEE);
            reservation.setDateTraitement(maintenant);
            reservation.setMotifAnnulation(MOTIF_ANNULATION);
            reservation.setMotifRefus(null);
        });
        reservationRepository.saveAll(expirees);
        log.info("{} demande(s) de réservation annulée(s) automatiquement après 72 h sans agent.", expirees.size());
    }
}
