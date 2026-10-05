package com.example.foncierback.repository;

import com.example.foncierback.entity.Reservation;
import com.example.foncierback.entity.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Réservations portant sur un bien rattaché à une société promotrice.
     *
     * Un bien peut être soit un LotProgramme (rattaché via ProgrammeFoncier),
     * soit une ParcelleIndividuelle (rattachée directement à la société).
     */
    @Query(value = """
            SELECT r.*
            FROM reservations r
            JOIN biens_fonciers b ON b.id = r.bien_id
            LEFT JOIN lots_programmes lp ON lp.id = b.id
            LEFT JOIN programmes_fonciers pf ON pf.id = lp.programme_id
            LEFT JOIN parcelles_individuelles pi ON pi.id = b.id
            WHERE pf.societe_id = :societeId
               OR pi.societe_id = :societeId
            ORDER BY r.date_reservation DESC
            """, nativeQuery = true)
    List<Reservation> findBySocieteId(@Param("societeId") Long societeId);
}
