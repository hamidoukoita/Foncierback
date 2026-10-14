package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.ReglesReservationResolver;
import com.example.foncierback.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final AcquereurRepository acquereurRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;
    private final ReglesReservationResolver reglesResolver;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        String numeroDossier = request.getNumeroDossier();
        if (numeroDossier == null || numeroDossier.isBlank()) {
            numeroDossier = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (reservationRepository.existsByNumeroDossier(numeroDossier)) {
            throw new BadRequestException("Une réservation avec le numéro de dossier '" + numeroDossier + "' existe déjà");
        }

        BienFoncier bien = bienFoncierRepository.findById(request.getBienId())
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + request.getBienId()));

        // Règle de la société : sans réservation multiple, un bien n'accepte qu'une réservation active.
        if (!reglesResolver.autoriseReservationMultiple(bien)
                && reservationRepository.existsByBienFoncierIdAndStatutIn(
                        bien.getId(), List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMER))) {
            throw new BadRequestException("Ce bien a déjà une réservation active et n'autorise pas les réservations multiples");
        }

        Acquereur acquereur = null;
        if (request.getAcquereurId() != null) {
            acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
        }

        // Une demande créée par l'acquéreur démarre toujours en attente et sans agent.
        // L'horodatage est généré côté serveur pour empêcher de contourner le délai d'attribution.
        LocalDateTime maintenant = LocalDateTime.now();
        Reservation reservation = Reservation.builder()
                .numeroDossier(numeroDossier)
                .dateReservation(maintenant)
                .dateExpiration(maintenant.plusHours(reglesResolver.heures(reglesResolver.societe(bien))))
                .statut(StatutReservation.EN_ATTENTE)
                .motifRefus(null)
                .motifAnnulation(null)
                .bienFoncier(bien)
                .acquereur(acquereur)
                .agentPromoteur(null)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse update(Long id, ReservationRequest request) {
        Reservation existing = findEntityById(id);
        expirerSiNecessairePuisRefuserAction(existing);

        if (request.getDateReservation() != null
                && !request.getDateReservation().equals(existing.getDateReservation())) {
            throw new BadRequestException("La date de création d'une réservation est immuable.");
        }
        if (request.getStatut() != null && request.getStatut() != existing.getStatut()) {
            throw new BadRequestException("Utilisez l'action dédiée pour changer le statut d'une réservation.");
        }
        if (request.getAgentId() != null
                && (existing.getAgentPromoteur() == null
                || !Objects.equals(existing.getAgentPromoteur().getId(), request.getAgentId()))) {
            throw new BadRequestException("Utilisez l'action dédiée d'attribution pour affecter un agent.");
        }

        if (request.getNumeroDossier() != null && !request.getNumeroDossier().isBlank()
                && !existing.getNumeroDossier().equalsIgnoreCase(request.getNumeroDossier())) {
            if (reservationRepository.existsByNumeroDossier(request.getNumeroDossier())) {
                throw new BadRequestException("Une réservation avec le numéro de dossier '" + request.getNumeroDossier() + "' existe déjà");
            }
            existing.setNumeroDossier(request.getNumeroDossier());
        }

        if (request.getBienId() != null && (existing.getBienFoncier() == null || !existing.getBienFoncier().getId().equals(request.getBienId()))) {
            BienFoncier bien = bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + request.getBienId()));
            existing.setBienFoncier(bien);
        }

        if (request.getAcquereurId() != null) {
            Acquereur acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
            existing.setAcquereur(acquereur);
        } else {
            existing.setAcquereur(null);
        }

        // L'agent, le statut et la date de demande sont gérés par leurs actions métier dédiées.
        existing.setMotifRefus(request.getMotifRefus());

        Reservation updated = reservationRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Reservation existing = findEntityById(id);
        reservationRepository.delete(existing);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse changerStatut(Long id, StatutReservation statut, String motifRefus) {
        Reservation existing = findEntityById(id);
        expirerSiNecessairePuisRefuserAction(existing);
        verifierDemandeEnAttente(existing);
        if (statut == StatutReservation.CONFIRMER) {
            verifierAgentAffecte(existing);
            verifierUniciteConfirmation(existing);
            existing.confirmer();
        } else if (statut == StatutReservation.REFUSER) {
            existing.refuser(motifRefus);
        } else {
            existing.setStatut(statut);
            existing.setMotifRefus(motifRefus);
            existing.setDateTraitement(LocalDateTime.now());
        }
        Reservation saved = reservationRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse confirmer(Long id) {
        Reservation existing = findEntityById(id);
        expirerSiNecessairePuisRefuserAction(existing);
        verifierDemandeEnAttente(existing);
        verifierAgentAffecte(existing);
        verifierUniciteConfirmation(existing);
        existing.confirmer();
        Reservation saved = reservationRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse refuser(Long id, String motifRefus) {
        Reservation existing = findEntityById(id);
        expirerSiNecessairePuisRefuserAction(existing);
        verifierDemandeEnAttente(existing);
        existing.refuser(motifRefus);
        Reservation saved = reservationRepository.save(existing);
        return mapToResponse(saved);
    }

    /**
     * Affecte réellement un agent au dossier. La demande doit encore être en attente,
     * dans son délai d'affectation, et l'agent doit appartenir à la société qui gère le bien.
     */
    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ReservationResponse assignerAgent(Long reservationId, Long agentId) {
        Reservation reservation = findEntityById(reservationId);
        expirerSiNecessairePuisRefuserAction(reservation);

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BadRequestException("Cette demande est déjà annulée et ne peut plus être attribuée.");
        }
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BadRequestException("Seules les demandes en attente peuvent être attribuées à un agent.");
        }

        AgentPromoteur agent = agentPromoteurRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + agentId));

        if (agent.getStatut() != StatutCompte.ACTIF) {
            throw new BadRequestException("Cet agent n'est pas actif et ne peut pas recevoir de réservation.");
        }

        Long societeBienId = trouverSocieteId(reservation.getBienFoncier());
        Long societeAgentId = agent.getSocietePromotrice() != null ? agent.getSocietePromotrice().getId() : null;
        // Si la société du bien n'est pas résolue (données incomplètes), on se base sur la société de l'utilisateur connecté.
        Long societeReferenceId = societeBienId != null ? societeBienId : SecurityUtils.getCurrentSocieteId();

        if (societeAgentId == null || (societeReferenceId != null && !Objects.equals(societeReferenceId, societeAgentId))) {
            throw new BadRequestException("L'agent sélectionné n'appartient pas à la société qui gère ce bien.");
        }

        if (!SecurityUtils.isAdmin()
                && societeReferenceId != null
                && !Objects.equals(SecurityUtils.getCurrentSocieteId(), societeReferenceId)) {
            throw new BadRequestException("Vous ne pouvez attribuer que les réservations de votre société.");
        }

        reservation.setAgentPromoteur(agent);
        reservation.setMotifAnnulation(null);
        return mapToResponse(reservationRepository.save(reservation));
    }

    private Long trouverSocieteId(BienFoncier bien) {
        SocietePromotrice societe = reglesResolver.societe(bien);
        return societe != null ? societe.getId() : null;
    }

    /**
     * Annule et persiste une demande qui a dépassé le délai d'attribution de sa société sans agent.
     * La transaction des opérations appelantes utilise noRollbackFor afin que l'annulation soit
     * conservée même si l'action tardive est ensuite refusée.
     */
    private void expirerSiNecessairePuisRefuserAction(Reservation reservation) {
        if (reglesResolver.estExpiree(reservation, LocalDateTime.now())) {
            reservation.annulerAutomatiquement(reglesResolver.motifAnnulation(reservation));
            reservationRepository.save(reservation);
            throw new BadRequestException("Cette demande a dépassé le délai de " + reglesResolver.heures(reservation)
                    + " heures sans agent affecté. Son statut est maintenant annulé.");
        }
    }

    private void verifierDemandeEnAttente(Reservation reservation) {
        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BadRequestException("Cette demande est annulée et ne peut plus être traitée.");
        }
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BadRequestException("Seules les demandes en attente peuvent être traitées.");
        }
    }

    private void verifierAgentAffecte(Reservation reservation) {
        if (reservation.getAgentPromoteur() == null) {
            throw new BadRequestException("Attribuez d'abord un agent à cette demande avant de la confirmer.");
        }
    }

    /** Un bien en réservation unique ne peut pas avoir deux réservations confirmées. */
    private void verifierUniciteConfirmation(Reservation reservation) {
        BienFoncier bien = reservation.getBienFoncier();
        if (bien != null && !reglesResolver.autoriseReservationMultiple(bien)
                && reservationRepository.existsByBienFoncierIdAndStatutAndIdNot(
                        bien.getId(), StatutReservation.CONFIRMER, reservation.getId())) {
            throw new BadRequestException("Ce bien a déjà une réservation confirmée et n'autorise pas les réservations multiples");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByAcquereur(Long acquereurId) {
        return reservationRepository.findByAcquereurId(acquereurId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByBien(Long bienId) {
        return reservationRepository.findByBienFoncierId(bienId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByAgent(Long agentId) {
        return reservationRepository.findByAgentPromoteurId(agentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findBySociete(Long societeId) {
        return reservationRepository.findBySocieteId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'identifiant : " + id));
    }

    private ReservationResponse mapToResponse(Reservation entity) {
        BienFoncier bien = entity.getBienFoncier();

        String bienDesignation = bien != null ? bien.designation() : null;
        String programmeNom = bien != null && bien.programmeParent() != null ? bien.programmeParent().getNom() : null;

        String acquereurNom = null;
        String acquereurTelephone = null;
        if (entity.getAcquereur() != null) {
            acquereurNom = (entity.getAcquereur().getPrenom() + " " + entity.getAcquereur().getNom()).trim();
            acquereurTelephone = entity.getAcquereur().getTelephone();
        }

        String agentNom = null;
        if (entity.getAgentPromoteur() != null) {
            agentNom = (entity.getAgentPromoteur().getPrenom() + " " + entity.getAgentPromoteur().getNom()).trim();
        }

        return ReservationResponse.builder()
                .id(entity.getId())
                .numeroDossier(entity.getNumeroDossier())
                .dateReservation(entity.getDateReservation())
                .dateExpiration(reglesResolver.echeance(entity))
                .statut(entity.getStatut())
                .motifRefus(entity.getMotifRefus())
                .motifAnnulation(entity.getMotifAnnulation())
                .dateTraitement(entity.getDateTraitement())
                .bienId(bien != null ? bien.getId() : null)
                .acquereurId(entity.getAcquereur() != null ? entity.getAcquereur().getId() : null)
                .agentId(entity.getAgentPromoteur() != null ? entity.getAgentPromoteur().getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .bienDesignation(bienDesignation)
                .programmeNom(programmeNom)
                .typeBien(bien != null ? bien.typeBien() : null)
                .montant(bien != null ? bien.getPrix() : null)
                .acquereurNom(acquereurNom)
                .acquereurTelephone(acquereurTelephone)
                .agentNom(agentNom)
                .build();
    }
}
