package com.example.foncierback.service;

import com.example.foncierback.dto.request.ReservationRequest;
import com.example.foncierback.dto.response.ReservationResponse;
import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.enums.StatutReservation;

import java.util.List;

public interface ReservationService {

    ReservationResponse create(ReservationRequest request);

    ReservationResponse getById(Long id);

    List<ReservationResponse> getAll();

    ReservationResponse update(Long id, ReservationRequest request);

    void delete(Long id);

    ReservationResponse changerStatut(Long id, StatutReservation statut, String motifRefus);

    ReservationResponse confirmer(Long id);

    ReservationResponse refuser(Long id, String motifRefus);

    List<ReservationResponse> findByAcquereur(Long acquereurId);

    List<ReservationResponse> findByBien(Long bienId);

    List<ReservationResponse> findBySociete(Long societeId);

    Reservation findEntityById(Long id);
}
