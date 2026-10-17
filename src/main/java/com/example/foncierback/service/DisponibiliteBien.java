package com.example.foncierback.service;

import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.enums.StatutParcelle;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.entity.RendezVous;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.repository.RendezVousRepository;
import com.example.foncierback.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Ce qu'un acquéreur peut encore faire sur un bien : le réserver, ou demander une visite.
 * Un bien vendu, indisponible ou en litige n'accepte ni l'un ni l'autre ; un bien dont le nombre
 * maximal de réservations en cours est atteint non plus, sauf la visite pour qui l'a déjà réservé.
 * Un acquéreur n'a qu'une réservation en cours et qu'un rendez-vous à venir par bien.
 */
@Component
@RequiredArgsConstructor
public class DisponibiliteBien {

    public static final List<StatutReservation> STATUTS_ACTIFS =
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.EN_COURS);

    private final ReservationRepository reservationRepository;
    private final ReglesReservationResolver reglesResolver;
    private final RendezVousRepository rendezVousRepository;

    private static final List<StatutRendezVous> RDV_VALABLES = List.of(StatutRendezVous.EN_ATTENTE, StatutRendezVous.ACCEPTER);
    private static final DateTimeFormatter FORMAT_RDV = DateTimeFormatter.ofPattern("dd/MM 'à' HH:mm");

    /** Rendez-vous déjà demandé ou accepté, à venir, de cet acquéreur pour ce bien. */
    public Optional<RendezVous> rendezVousPrevu(BienFoncier bien, Long acquereurId) {
        if (acquereurId == null) return Optional.empty();
        return rendezVousRepository.findFirstByAcquereurIdAndBienFoncierIdAndStatutInAndDateRendezVousAfterOrderByDateRendezVousAsc(
                acquereurId, bien.getId(), RDV_VALABLES, LocalDateTime.now());
    }

    public long reservationsEnCours(BienFoncier bien) {
        return reservationRepository.countByBienFoncierIdAndStatutIn(bien.getId(), STATUTS_ACTIFS);
    }

    public boolean aDejaReserve(BienFoncier bien, Long acquereurId) {
        return acquereurId != null
                && reservationRepository.existsByAcquereurIdAndBienFoncierIdAndStatutIn(acquereurId, bien.getId(), STATUTS_ACTIFS);
    }

    /** Raison pour laquelle le bien ne peut plus être réservé, null s'il le peut. */
    public String motifReservationImpossible(BienFoncier bien) {
        String motif = motifStatut(bien);
        if (motif != null) return motif;
        int max = reglesResolver.reservationsMax(bien);
        if (reservationsEnCours(bien) >= max) {
            return max == 1
                    ? "Ce bien est déjà réservé et n'accepte qu'une réservation à la fois."
                    : "Le nombre maximal de réservations (" + max + ") est atteint pour ce bien.";
        }
        return null;
    }

    /** Raison pour laquelle une visite ne peut plus être demandée, null si elle le peut. */
    public String motifVisiteImpossible(BienFoncier bien, Long acquereurId) {
        String motif = motifStatut(bien);
        if (motif != null) return motif;
        Optional<RendezVous> prevu = rendezVousPrevu(bien, acquereurId);
        if (prevu.isPresent()) {
            return "Vous avez déjà un rendez-vous prévu pour ce bien le "
                    + prevu.get().getDateRendezVous().format(FORMAT_RDV) + ".";
        }
        if (aDejaReserve(bien, acquereurId)) return null;
        return reservationsEnCours(bien) >= reglesResolver.reservationsMax(bien)
                ? "Ce bien n'accepte plus de nouvelle réservation : la prise de rendez-vous est fermée."
                : null;
    }

    private String motifStatut(BienFoncier bien) {
        StatutParcelle statut = bien.getStatut();
        if (statut == StatutParcelle.VENDUE) return "Ce bien est vendu.";
        if (statut != StatutParcelle.DISPONIBLE && statut != StatutParcelle.RESERVER) {
            return "Ce bien n'est pas disponible pour le moment.";
        }
        return null;
    }
}
