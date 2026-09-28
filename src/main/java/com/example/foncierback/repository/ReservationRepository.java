package com.example.foncierback.repository;

import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    Optional<Reservation> findByNumeroDossier(String numeroDossier);

    boolean existsByNumeroDossier(String numeroDossier);

    List<Reservation> findByAcquereurId(Long acquereurId);

    List<Reservation> findByBienFoncierId(Long bienId);

    List<Reservation> findByAgentPromoteurId(Long agentId);

    List<Reservation> findByStatut(StatutReservation statut);

    List<Reservation> findByAcquereurIdAndStatut(Long acquereurId, StatutReservation statut);
}
