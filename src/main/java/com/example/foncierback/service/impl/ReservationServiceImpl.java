package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.enums.StatutReservation;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.ReservationRepository;
import com.example.foncierback.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final AcquereurRepository acquereurRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;

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

        // Règle d'autorisation : un bien non « réservable plusieurs fois » n'accepte qu'une réservation active.
        if (!Boolean.TRUE.equals(bien.getReservationMultiple())
                && reservationRepository.existsByBienFoncierIdAndStatutIn(
                        bien.getId(), List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMER))) {
            throw new BadRequestException("Ce bien a déjà une réservation active et n'autorise pas les réservations multiples");
        }

        Acquereur acquereur = null;
        if (request.getAcquereurId() != null) {
            acquereur = acquereurRepository.findById(request.getAcquereurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + request.getAcquereurId()));
        }

        AgentPromoteur agent = null;
        if (request.getAgentId() != null) {
            agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
        }

        LocalDateTime dateRes = request.getDateReservation() != null ? request.getDateReservation() : LocalDateTime.now();
        StatutReservation statut = request.getStatut() != null ? request.getStatut() : StatutReservation.EN_ATTENTE;

        Reservation reservation = Reservation.builder()
                .numeroDossier(numeroDossier)
                .dateReservation(dateRes)
                .statut(statut)
                .motifRefus(request.getMotifRefus())
                .bienFoncier(bien)
                .acquereur(acquereur)
                .agentPromoteur(agent)
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
    public ReservationResponse update(Long id, ReservationRequest request) {
        Reservation existing = findEntityById(id);

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

        if (request.getAgentId() != null) {
            AgentPromoteur agent = agentPromoteurRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'identifiant : " + request.getAgentId()));
            existing.setAgentPromoteur(agent);
        } else {
            existing.setAgentPromoteur(null);
        }

        if (request.getDateReservation() != null) {
            existing.setDateReservation(request.getDateReservation());
        }

        if (request.getStatut() != null) {
            existing.setStatut(request.getStatut());
        }

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
    public ReservationResponse changerStatut(Long id, StatutReservation statut, String motifRefus) {
        Reservation existing = findEntityById(id);
        if (statut == StatutReservation.CONFIRMER) {
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
    public ReservationResponse confirmer(Long id) {
        Reservation existing = findEntityById(id);
        verifierUniciteConfirmation(existing);
        existing.confirmer();
        Reservation saved = reservationRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public ReservationResponse refuser(Long id, String motifRefus) {
        Reservation existing = findEntityById(id);
        existing.refuser(motifRefus);
        Reservation saved = reservationRepository.save(existing);
        return mapToResponse(saved);
    }

    /** Un bien en réservation unique ne peut pas avoir deux réservations confirmées. */
    private void verifierUniciteConfirmation(Reservation reservation) {
        BienFoncier bien = reservation.getBienFoncier();
        if (bien != null && !Boolean.TRUE.equals(bien.getReservationMultiple())
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

        String bienDesignation = null;
        String programmeNom = null;
        if (bien instanceof LotProgramme lot) {
            bienDesignation = lot.getNumeroLot();
            if (lot.getProgrammeFoncier() != null) {
                programmeNom = lot.getProgrammeFoncier().getNom();
            }
        } else if (bien instanceof ParcelleIndividuelle parcelle) {
            bienDesignation = parcelle.getReference();
        }

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
                .statut(entity.getStatut())
                .motifRefus(entity.getMotifRefus())
                .dateTraitement(entity.getDateTraitement())
                .bienId(bien != null ? bien.getId() : null)
                .acquereurId(entity.getAcquereur() != null ? entity.getAcquereur().getId() : null)
                .agentId(entity.getAgentPromoteur() != null ? entity.getAgentPromoteur().getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .bienDesignation(bienDesignation)
                .programmeNom(programmeNom)
                .montant(bien != null ? bien.getPrix() : null)
                .acquereurNom(acquereurNom)
                .acquereurTelephone(acquereurTelephone)
                .agentNom(agentNom)
                .build();
    }
}
