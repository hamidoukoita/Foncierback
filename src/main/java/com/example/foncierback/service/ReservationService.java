package com.example.foncierback.service;

import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.Reservation;

import java.util.List;

public interface ReservationService {

    /** Réserve un bien ; le paiement Orange Money est simulé (lots et parcelles). */
    ReservationResponse create(ReservationRequest request);

    /** Montant, durée et disponibilité d'un bien avant sa réservation. */
    com.example.foncierback.dto.response.ConditionsReservationResponse conditions(Long bienId);

    ReservationResponse getById(Long id);

    List<ReservationResponse> getAll();

    ReservationResponse update(Long id, ReservationRequest request);

    void delete(Long id);

    ReservationResponse assignerAgent(Long reservationId, Long agentId);

    /** L'agent chargé du dossier démarre le décompte : le bien est bloqué jusqu'à l'échéance. */
    ReservationResponse demarrerDecompte(Long id);

    /** Vente conclue : le bien passe « vendu », les autres réservations sont remboursées. */
    ReservationResponse conclureVente(Long id);

    /** Désistement de l'acquéreur, sans remboursement. */
    ReservationResponse annulerParAcquereur(Long id);

    /** Fait expirer les réservations dont le décompte est échu ; retourne leur nombre. */
    int expirerReservationsEchues();

    List<ReservationResponse> findByAcquereur(Long acquereurId);

    List<ReservationResponse> findByBien(Long bienId);

    List<ReservationResponse> findByAgent(Long agentId);

    List<ReservationResponse> findBySociete(Long societeId);

    Reservation findEntityById(Long id);
}
