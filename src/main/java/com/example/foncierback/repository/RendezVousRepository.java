package com.example.foncierback.repository;

import com.example.foncierback.entity.RendezVous;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.TypeRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long>, JpaSpecificationExecutor<RendezVous> {

    List<RendezVous> findByAcquereurId(Long acquereurId);

    List<RendezVous> findByAgentPromoteurId(Long agentId);

    List<RendezVous> findByReservationId(Long reservationId);

    List<RendezVous> findByBienFoncierId(Long bienId);

    List<RendezVous> findByCreneauId(Long creneauId);

    List<RendezVous> findByStatut(StatutRendezVous statut);

    List<RendezVous> findByTypeRDV(TypeRDV typeRDV);

    List<RendezVous> findByDateRendezVousBetween(LocalDateTime debut, LocalDateTime fin);

    List<RendezVous> findByAgentPromoteurIdAndDateRendezVousBetween(Long agentId, LocalDateTime debut, LocalDateTime fin);
}
