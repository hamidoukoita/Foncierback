package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ConditionsReservationResponse;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.entity.enums.StatutParcelle;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.DisponibiliteBien;
import com.example.foncierback.service.ReglesReservationResolver;
import com.example.foncierback.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Cycle d'une réservation :
 * paiement (simulé) → en attente d'un agent → décompte démarré par l'agent → vendue avant l'échéance,
 * ou expirée à l'échéance (montant non rendu). L'acquéreur peut se désister tant que le bien
 * n'est pas vendu, sans remboursement. La société ne refuse pas une réservation : tout bien
 * disponible est réservable. Le statut du bien suit ses réservations (réservé, vendu, disponible).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final String MOYEN_ORANGE_MONEY = "ORANGE_MONEY";
    private static final String ALPHABET_REFERENCE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final DateTimeFormatter FORMAT_REFERENCE = DateTimeFormatter.ofPattern("yyMMdd");
    private static final SecureRandom ALEA = new SecureRandom();

    private static final String TYPE_RESERVATION = "RESERVATION";
    private static final String LIBELLE_RESERVATION = "Réservation";

    private final ReservationRepository reservationRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final AcquereurRepository acquereurRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;
    private final ReglesReservationResolver reglesResolver;
    private final NotificationDispatchService notifications;
    private final DisponibiliteBien disponibilite;

    // ------------------------------------------------------------------ Création et paiement

    @Override
    public ReservationResponse create(ReservationRequest request) {
        BienFoncier bien = bienFoncierRepository.findById(request.getBienId())
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + request.getBienId()));
        Acquereur acquereur = resoudreAcquereur(request.getAcquereurId());

        if (acquereur != null && disponibilite.aDejaReserve(bien, acquereur.getId())) {
            throw new BadRequestException("Vous avez déjà une réservation en cours sur ce bien.");
        }
        String motif = disponibilite.motifReservationImpossible(bien);
        if (motif != null) {
            throw new BadRequestException(motif);
        }

        LocalDateTime maintenant = LocalDateTime.now();
        BigDecimal montant = reglesResolver.prix(bien);
        boolean payante = montant.signum() > 0;

        Reservation reservation = Reservation.builder()
                .numeroDossier(numeroDossierUnique(request.getNumeroDossier()))
                .dateReservation(maintenant)
                .statut(StatutReservation.EN_ATTENTE)
                .bienFoncier(bien)
                .acquereur(acquereur)
                .montantReservation(montant)
                // Paiement simulé : aucun appel à Orange Money, la transaction est réputée acceptée.
                .moyenPaiement(payante ? MOYEN_ORANGE_MONEY : null)
                .referencePaiement(payante ? referencePaiement(maintenant) : null)
                .datePaiement(payante ? maintenant : null)
                .build();
        Reservation saved = reservationRepository.save(reservation);

        if (bien.getStatut() == StatutParcelle.DISPONIBLE) {
            bien.setStatut(StatutParcelle.RESERVER);
            bienFoncierRepository.save(bien);
        }

        notifications.notifySocietePourBien(bien, TYPE_RESERVATION, LIBELLE_RESERVATION,
                "Nouvelle réservation",
                nomAcquereur(saved) + " a réservé " + bien.designation()
                        + (payante ? " (" + montant.toPlainString() + " FCFA payés)" : "")
                        + ". Attribuez un agent pour suivre le dossier.");
        return mapToResponse(saved);
    }

    /** Un acquéreur réserve toujours pour lui-même ; l'administrateur peut désigner l'acquéreur. */
    private Acquereur resoudreAcquereur(Long acquereurDemande) {
        Long id = SecurityUtils.isAcquereur() ? SecurityUtils.getCurrentUserId() : acquereurDemande;
        if (id == null) return null;
        return acquereurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ConditionsReservationResponse conditions(Long bienId) {
        BienFoncier bien = bienFoncierRepository.findById(bienId)
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + bienId));
        BigDecimal montant = reglesResolver.prix(bien);
        Long acquereurId = SecurityUtils.isAcquereur() ? SecurityUtils.getCurrentUserId() : null;
        boolean dejaReserve = disponibilite.aDejaReserve(bien, acquereurId);
        String motif = dejaReserve
                ? "Vous avez déjà une réservation en cours sur ce bien."
                : disponibilite.motifReservationImpossible(bien);
        String motifVisite = disponibilite.motifVisiteImpossible(bien, acquereurId);
        SocietePromotrice societe = reglesResolver.societe(bien);
        return ConditionsReservationResponse.builder()
                .bienId(bien.getId())
                .montant(montant)
                .payante(montant.signum() > 0)
                .dureeHeures(reglesResolver.heures(societe))
                .reservationMultiple(reglesResolver.autoriseReservationMultiple(societe))
                .reservationsMax(reglesResolver.reservationsMax(societe))
                .reservationsEnCours(disponibilite.reservationsEnCours(bien))
                .dejaReserveParMoi(dejaReserve)
                .reservable(motif == null)
                .motifIndisponibilite(motif)
                .visitePossible(motifVisite == null)
                .motifVisiteImpossible(motifVisite)
                .vendu(bien.getStatut() == StatutParcelle.VENDUE)
                .societeNom(societe != null ? societe.getNom() : null)
                .build();
    }

    private String numeroDossierUnique(String demande) {
        if (demande == null || demande.isBlank()) {
            return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (reservationRepository.existsByNumeroDossier(demande)) {
            throw new BadRequestException("Une réservation avec le numéro de dossier '" + demande + "' existe déjà");
        }
        return demande;
    }

    /** Référence de transaction simulée, au format d'un reçu Orange Money : OM-260415-7KQ2PX. */
    private String referencePaiement(LocalDateTime date) {
        StringBuilder suffixe = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            suffixe.append(ALPHABET_REFERENCE.charAt(ALEA.nextInt(ALPHABET_REFERENCE.length())));
        }
        return "OM-" + date.format(FORMAT_REFERENCE) + "-" + suffixe;
    }

    // ------------------------------------------------------------------ Suivi par la société

    /** Affecte (ou change) l'agent qui suit le dossier : il doit être actif et de la société du bien. */
    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse assignerAgent(Long reservationId, Long agentId) {
        Reservation reservation = findEntityById(reservationId);
        expirerSiEchue(reservation);
        if (!reservation.estActive()) {
            throw new BadRequestException("Seules les réservations en cours peuvent être attribuées à un agent.");
        }

        AgentPromoteur agent = agentPromoteurRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + agentId));
        if (agent.getStatut() != StatutCompte.ACTIF) {
            throw new BadRequestException("Cet agent n'est pas actif et ne peut pas recevoir de réservation.");
        }

        Long societeBienId = societeId(reservation.getBienFoncier());
        Long societeAgentId = agent.getSocietePromotrice() != null ? agent.getSocietePromotrice().getId() : null;
        Long societeReferenceId = societeBienId != null ? societeBienId : SecurityUtils.getCurrentSocieteId();
        if (societeAgentId == null || (societeReferenceId != null && !Objects.equals(societeReferenceId, societeAgentId))) {
            throw new BadRequestException("L'agent sélectionné n'appartient pas à la société qui gère ce bien.");
        }
        if (!SecurityUtils.isAdmin() && societeReferenceId != null
                && !Objects.equals(SecurityUtils.getCurrentSocieteId(), societeReferenceId)) {
            throw new AccessDeniedException("Vous ne pouvez attribuer que les réservations de votre société.");
        }

        reservation.setAgentPromoteur(agent);
        Reservation saved = reservationRepository.save(reservation);
        notifications.notifyUser(agent, TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation à suivre",
                "Vous suivez la réservation " + saved.getNumeroDossier() + " (" + designation(saved)
                        + "). Démarrez le décompte quand vous prenez contact avec l'acquéreur.");
        // L'acquéreur sait qui le suivra et comment le joindre.
        String nomAgent = (agent.getPrenom() + " " + agent.getNom()).trim();
        notifications.notifyUser(saved.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Votre dossier est confirmé",
                "Votre réservation " + saved.getNumeroDossier() + " (" + designation(saved) + ") est confiée à "
                        + nomAgent + (agent.getTelephone() != null ? ", joignable au " + agent.getTelephone() : "")
                        + ". Votre agent vous contactera pour la suite.");
        return mapToResponse(saved);
    }

    @Override
    public ReservationResponse demarrerDecompte(Long id) {
        Reservation reservation = findEntityById(id);
        // C'est l'agent chargé du dossier qui décide du moment où le délai commence.
        if (!SecurityUtils.isAdmin() && (reservation.getAgentPromoteur() == null
                || !Objects.equals(reservation.getAgentPromoteur().getId(), SecurityUtils.getCurrentUserId()))) {
            throw new AccessDeniedException("Seul l'agent chargé du dossier peut démarrer le décompte.");
        }
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BadRequestException("Le décompte ne peut être démarré que sur une réservation en attente.");
        }
        if (reservation.getAgentPromoteur() == null) {
            throw new BadRequestException("Attribuez d'abord un agent à cette réservation.");
        }

        int duree = reglesResolver.heures(reservation);
        reservation.demarrerDecompte(duree);
        Reservation saved = reservationRepository.save(reservation);
        notifications.notifyUser(saved.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Votre réservation est lancée",
                "Votre agent a démarré le suivi de " + designation(saved) + ". Le bien vous est réservé pendant "
                        + duree + " heures pour conclure la vente.");
        return mapToResponse(saved);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse conclureVente(Long id) {
        Reservation reservation = findEntityById(id);
        verifierSuiviAutorise(reservation);
        expirerSiEchue(reservation);
        if (!reservation.estActive()) {
            throw new BadRequestException("Seule une réservation en cours peut aboutir à une vente.");
        }

        reservation.conclureVente();
        Reservation saved = reservationRepository.save(reservation);
        BienFoncier bien = saved.getBienFoncier();
        bien.setStatut(StatutParcelle.VENDUE);
        bienFoncierRepository.save(bien);

        // Les autres réservations du bien ne peuvent plus aboutir : elles sont remboursées.
        List<Reservation> autres = reservationRepository.findByBienFoncierIdAndStatutInAndIdNot(bien.getId(), DisponibiliteBien.STATUTS_ACTIFS, saved.getId());
        for (Reservation autre : autres) {
            autre.annulerAvecRemboursement("Le bien a été vendu à un autre acquéreur : votre réservation vous est remboursée.");
            notifications.notifyUser(autre.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation remboursée",
                    designation(autre) + " a été vendu à un autre acquéreur. Le montant de votre réservation vous est remboursé.");
        }
        reservationRepository.saveAll(autres);

        notifications.notifyUser(saved.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Félicitations !",
                "La vente de " + designation(saved) + " est conclue.");
        return mapToResponse(saved);
    }

    /** Seuls l'agent chargé du dossier, le responsable de la société du bien ou l'administrateur suivent le dossier. */
    private void verifierSuiviAutorise(Reservation reservation) {
        if (SecurityUtils.isAdmin()) return;
        Long courant = SecurityUtils.getCurrentUserId();
        boolean agentDuDossier = reservation.getAgentPromoteur() != null
                && Objects.equals(reservation.getAgentPromoteur().getId(), courant);
        boolean responsable = SecurityUtils.isResponsableSociete()
                && Objects.equals(SecurityUtils.getCurrentSocieteId(), societeId(reservation.getBienFoncier()));
        if (!agentDuDossier && !responsable) {
            throw new AccessDeniedException("Seul l'agent chargé du dossier ou le responsable de la société peut le faire.");
        }
    }

    // ------------------------------------------------------------------ Acquéreur

    @Override
    public ReservationResponse annulerParAcquereur(Long id) {
        Reservation reservation = findEntityById(id);
        if (!SecurityUtils.isAdmin() && (reservation.getAcquereur() == null
                || !Objects.equals(reservation.getAcquereur().getId(), SecurityUtils.getCurrentUserId()))) {
            throw new AccessDeniedException("Vous ne pouvez annuler que vos propres réservations.");
        }
        if (!reservation.estActive()) {
            throw new BadRequestException("Cette réservation n'est plus en cours et ne peut pas être annulée.");
        }

        reservation.annulerParAcquereur();
        Reservation saved = reservationRepository.save(reservation);
        libererBienSiPlusReserve(saved.getBienFoncier());

        String message = nomAcquereur(saved) + " a annulé sa réservation " + saved.getNumeroDossier()
                + " (" + designation(saved) + ").";
        if (saved.getAgentPromoteur() != null) {
            notifications.notifyUser(saved.getAgentPromoteur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation annulée", message);
        } else {
            notifications.notifySocietePourBien(saved.getBienFoncier(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation annulée", message);
        }
        return mapToResponse(saved);
    }

    // ------------------------------------------------------------------ Échéances

    @Override
    public int expirerReservationsEchues() {
        List<Reservation> echues = reservationRepository
                .findByStatutAndDateExpirationLessThanEqual(StatutReservation.EN_COURS, LocalDateTime.now());
        echues.forEach(this::expirer);
        return echues.size();
    }

    /** Une action tardive sur une réservation échue l'expire d'abord (conservé malgré l'exception). */
    private void expirerSiEchue(Reservation reservation) {
        if (reservation.getStatut() == StatutReservation.EN_COURS && reservation.getDateExpiration() != null
                && !reservation.getDateExpiration().isAfter(LocalDateTime.now())) {
            expirer(reservation);
            throw new BadRequestException("Le délai de cette réservation est écoulé : elle a expiré.");
        }
    }

    private void expirer(Reservation reservation) {
        reservation.expirer();
        reservationRepository.save(reservation);
        libererBienSiPlusReserve(reservation.getBienFoncier());
        notifications.notifyUser(reservation.getAcquereur(), TYPE_RESERVATION, LIBELLE_RESERVATION, "Réservation expirée",
                "Le délai de votre réservation sur " + designation(reservation)
                        + " est écoulé sans vente. Le bien est de nouveau disponible.");
    }

    /** Le bien redevient disponible quand plus aucune réservation ne le bloque. */
    private void libererBienSiPlusReserve(BienFoncier bien) {
        if (bien != null && bien.getStatut() == StatutParcelle.RESERVER
                && !reservationRepository.existsByBienFoncierIdAndStatutIn(bien.getId(), DisponibiliteBien.STATUTS_ACTIFS)) {
            bien.setStatut(StatutParcelle.DISPONIBLE);
            bienFoncierRepository.save(bien);
        }
    }

    // ------------------------------------------------------------------ Administration

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        Reservation existing = findEntityById(id);
        if (request.getStatut() != null && request.getStatut() != existing.getStatut()) {
            throw new BadRequestException("Utilisez l'action dédiée pour changer le statut d'une réservation.");
        }
        if (request.getBienId() != null && !Objects.equals(existing.getBienFoncier().getId(), request.getBienId())) {
            throw new BadRequestException("Le bien d'une réservation ne peut pas être changé.");
        }
        if (request.getNumeroDossier() != null && !request.getNumeroDossier().isBlank()
                && !existing.getNumeroDossier().equalsIgnoreCase(request.getNumeroDossier())) {
            existing.setNumeroDossier(numeroDossierUnique(request.getNumeroDossier()));
        }
        return mapToResponse(reservationRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Reservation existing = findEntityById(id);
        BienFoncier bien = existing.getBienFoncier();
        reservationRepository.delete(existing);
        reservationRepository.flush();
        libererBienSiPlusReserve(bien);
    }

    // ------------------------------------------------------------------ Lectures

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByAcquereur(Long acquereurId) {
        return reservationRepository.findByAcquereurId(acquereurId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByBien(Long bienId) {
        return reservationRepository.findByBienFoncierId(bienId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByAgent(Long agentId) {
        return reservationRepository.findByAgentPromoteurId(agentId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findBySociete(Long societeId) {
        return reservationRepository.findBySocieteId(societeId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'identifiant : " + id));
    }

    // ------------------------------------------------------------------ Outils

    private Long societeId(BienFoncier bien) {
        SocietePromotrice societe = reglesResolver.societe(bien);
        return societe != null ? societe.getId() : null;
    }

    private String designation(Reservation reservation) {
        BienFoncier bien = reservation.getBienFoncier();
        return bien != null && bien.designation() != null ? bien.designation() : "le bien";
    }

    private String nomAcquereur(Reservation reservation) {
        Acquereur a = reservation.getAcquereur();
        return a == null ? "Un acquéreur" : (a.getPrenom() + " " + a.getNom()).trim();
    }

    private ReservationResponse mapToResponse(Reservation entity) {
        BienFoncier bien = entity.getBienFoncier();
        Acquereur acquereur = entity.getAcquereur();
        AgentPromoteur agent = entity.getAgentPromoteur();

        return ReservationResponse.builder()
                .id(entity.getId())
                .numeroDossier(entity.getNumeroDossier())
                .dateReservation(entity.getDateReservation())
                .dateExpiration(entity.getDateExpiration())
                .dateDebutDecompte(entity.getDateDebutDecompte())
                .dateConclusion(entity.getDateConclusion())
                .dureeReservationHeures(bien != null ? reglesResolver.heures(entity) : null)
                .statut(entity.getStatut())
                .motifRefus(entity.getMotifRefus())
                .motifAnnulation(entity.getMotifAnnulation())
                .dateTraitement(entity.getDateTraitement())
                .bienId(bien != null ? bien.getId() : null)
                .acquereurId(acquereur != null ? acquereur.getId() : null)
                .agentId(agent != null ? agent.getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .bienDesignation(bien != null ? bien.designation() : null)
                .programmeNom(bien != null && bien.programmeParent() != null ? bien.programmeParent().getNom() : null)
                .typeBien(bien != null ? bien.typeBien() : null)
                .montant(bien != null ? bien.getPrix() : null)
                .acquereurNom(acquereur != null ? (acquereur.getPrenom() + " " + acquereur.getNom()).trim() : null)
                .acquereurTelephone(acquereur != null ? acquereur.getTelephone() : null)
                .agentNom(agent != null ? (agent.getPrenom() + " " + agent.getNom()).trim() : null)
                .agentTelephone(agent != null ? agent.getTelephone() : null)
                .montantReservation(entity.getMontantReservation())
                .moyenPaiement(entity.getMoyenPaiement())
                .referencePaiement(entity.getReferencePaiement())
                .datePaiement(entity.getDatePaiement())
                .rembourse(Boolean.TRUE.equals(entity.getRembourse()))
                .dateRemboursement(entity.getDateRemboursement())
                .build();
    }
}
