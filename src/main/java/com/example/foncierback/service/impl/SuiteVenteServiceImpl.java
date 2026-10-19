package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.response.BienRemplacementResponse;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutParcelle;
import com.example.foncierback.entity.enums.SuiteVente;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.LotProgrammeRepository;
import com.example.foncierback.repository.MaisonCleEnMainRepository;
import com.example.foncierback.repository.ParcelleIndividuelleRepository;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.CouvertureResolver;
import com.example.foncierback.service.DisponibiliteBien;
import com.example.foncierback.service.ReglesReservationResolver;
import com.example.foncierback.service.ReservationService;
import com.example.foncierback.service.SuiteVenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SuiteVenteServiceImpl implements SuiteVenteService {

    private static final List<StatutParcelle> STATUTS_RESERVABLES = List.of(StatutParcelle.DISPONIBLE, StatutParcelle.RESERVER);
    private static final String TYPE_RESERVATION = "RESERVATION";
    private static final String LIBELLE_RESERVATION = "Réservation";

    private final ReservationRepository reservationRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final LotProgrammeRepository lotRepository;
    private final ParcelleIndividuelleRepository parcelleRepository;
    private final MaisonCleEnMainRepository maisonRepository;
    private final ReservationService reservationService;
    private final ReglesReservationResolver reglesResolver;
    private final DisponibiliteBien disponibilite;
    private final CouvertureResolver couvertures;
    private final NotificationDispatchService notifications;

    @Override
    @Transactional(readOnly = true)
    public List<BienRemplacementResponse> biensDeRemplacement(Long reservationId) {
        Reservation reservation = reservationAChoisir(reservationId);
        SocietePromotrice societe = reglesResolver.societe(reservation.getBienFoncier());
        if (societe == null) return List.of();
        Long acquereurId = reservation.getAcquereur() != null ? reservation.getAcquereur().getId() : null;

        List<BienFoncier> biens = new ArrayList<>();
        biens.addAll(lotRepository.findByProgrammeFoncierSocietePromotriceIdAndStatutIn(societe.getId(), STATUTS_RESERVABLES));
        biens.addAll(parcelleRepository.findBySocietePromotriceIdAndStatutIn(societe.getId(), STATUTS_RESERVABLES));
        biens.addAll(maisonRepository.findBySocietePromotriceIdAndStatutIn(societe.getId(), STATUTS_RESERVABLES));
        return biens.stream()
                .filter(b -> disponibilite.motifReservationImpossible(b) == null && !disponibilite.aDejaReserve(b, acquereurId))
                .sorted(Comparator.comparing(BienFoncier::typeBien).thenComparing(BienFoncier::getPrix))
                .map(this::versRemplacement)
                .toList();
    }

    @Override
    public ReservationResponse demanderRemboursement(Long reservationId) {
        Reservation reservation = reservationAChoisir(reservationId);
        reservation.demanderRemboursement(DELAI_REMBOURSEMENT_HEURES);
        reservationRepository.save(reservation);
        notifications.notifyUser(reservation.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Remboursement demandé",
                "Le montant de votre réservation " + reservation.getNumeroDossier()
                        + " vous sera remboursé sur votre compte Orange Money sous 24 h.");
        return reservationService.getById(reservation.getId());
    }

    @Override
    public ReservationResponse remplacer(Long reservationId, Long bienId) {
        Reservation origine = reservationAChoisir(reservationId);
        BienFoncier bien = bienFoncierRepository.findById(bienId)
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + bienId));
        SocietePromotrice societeOrigine = reglesResolver.societe(origine.getBienFoncier());
        SocietePromotrice societeBien = reglesResolver.societe(bien);
        if (societeOrigine == null || societeBien == null || !Objects.equals(societeOrigine.getId(), societeBien.getId())) {
            throw new BadRequestException("Le report n'est possible que sur un bien de la même société.");
        }

        Reservation nouvelle = reservationService.reporterSurBien(origine, bien);
        origine.remplacerPar(nouvelle.getId());
        reservationRepository.save(origine);
        return reservationService.getById(nouvelle.getId());
    }

    @Override
    public int traiterEcheances() {
        LocalDateTime maintenant = LocalDateTime.now();
        List<Reservation> sansChoix = reservationRepository.findBySuiteVenteAndDateLimiteChoixLessThanEqual(SuiteVente.A_CHOISIR, maintenant);
        List<Reservation> echus = reservationRepository.findBySuiteVenteAndDateRemboursementPrevueLessThanEqual(SuiteVente.REMBOURSEMENT_EN_COURS, maintenant);
        List<Reservation> aRembourser = Stream.concat(sansChoix.stream(), echus.stream()).toList();
        for (Reservation reservation : aRembourser) {
            reservation.rembourser();
            notifications.notifyUser(reservation.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation remboursée",
                    "Le montant de votre réservation " + reservation.getNumeroDossier()
                            + " vous a été remboursé sur votre compte Orange Money.");
        }
        reservationRepository.saveAll(aRembourser);
        return aRembourser.size();
    }

    /** Réservation de l'acquéreur connecté, en attente de son choix. */
    private Reservation reservationAChoisir(Long reservationId) {
        Reservation reservation = reservationService.findEntityById(reservationId);
        if (!SecurityUtils.isAdmin() && (reservation.getAcquereur() == null
                || !Objects.equals(reservation.getAcquereur().getId(), SecurityUtils.getCurrentUserId()))) {
            throw new AccessDeniedException("Cette réservation ne vous appartient pas.");
        }
        if (reservation.getSuiteVente() != SuiteVente.A_CHOISIR) {
            throw new BadRequestException("Le choix pour cette réservation a déjà été fait.");
        }
        return reservation;
    }

    private BienRemplacementResponse versRemplacement(BienFoncier bien) {
        return BienRemplacementResponse.builder()
                .id(bien.getId())
                .typeBien(bien.typeBien())
                .designation(bien.designation())
                .reference(bien.getReference())
                .programmeNom(bien.programmeParent() != null ? bien.programmeParent().getNom() : null)
                .superficie(bien.getSuperficie())
                .prix(bien.getPrix())
                .imageUrl(couvertures.pourBien(bien.getId()))
                .build();
    }
}
