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

    long countByAgentPromoteurId(Long agentId);

    // --- Indicateurs d'un agent : agrégats calculés par la base, sans charger les dossiers ---
    // Période : dossiers attribués (ou, à défaut, réservés) depuis :debut.

    @Query("""
            select r.statut, count(r) from Reservation r
            where r.agentPromoteur.id = :agentId and coalesce(r.dateAttribution, r.dateReservation) >= :debut
            group by r.statut
            """)
    List<Object[]> compterParStatutPourAgent(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut);

    @Query("""
            select coalesce(sum(b.prix), 0) from Reservation r join r.bienFoncier b
            where r.agentPromoteur.id = :agentId and coalesce(r.dateAttribution, r.dateReservation) >= :debut
              and r.statut = :vendue
            """)
    java.math.BigDecimal chiffreAffairesAgent(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut,
                                              @Param("vendue") StatutReservation vendue);

    /** Montants conservés : hors remboursements, et un report n'est compté que sur la réservation d'origine. */
    @Query("""
            select coalesce(sum(r.montantReservation), 0) from Reservation r
            where r.agentPromoteur.id = :agentId and coalesce(r.dateAttribution, r.dateReservation) >= :debut
              and r.rembourse = false and r.reservationOrigineId is null
              and (r.suiteVente is null or r.suiteVente not in :suitesRemboursees)
            """)
    java.math.BigDecimal reservationsEncaisseesAgent(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut,
                                                     @Param("suitesRemboursees") java.util.Collection<com.example.foncierback.entity.enums.SuiteVente> suitesRemboursees);

    @Query(value = """
            SELECT AVG(TIMESTAMPDIFF(MINUTE, date_attribution, date_debut_decompte)) FROM reservations
            WHERE agent_id = :agentId AND COALESCE(date_attribution, date_reservation) >= :debut
              AND date_attribution IS NOT NULL AND date_debut_decompte >= date_attribution
            """, nativeQuery = true)
    Double delaiMoyenPriseEnChargeMinutes(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut);

    @Query(value = """
            SELECT AVG(TIMESTAMPDIFF(MINUTE, date_debut_decompte, date_conclusion)) FROM reservations
            WHERE agent_id = :agentId AND COALESCE(date_attribution, date_reservation) >= :debut
              AND date_debut_decompte IS NOT NULL AND date_conclusion >= date_debut_decompte
            """, nativeQuery = true)
    Double delaiMoyenVenteMinutes(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut);

    @Query(value = """
            select r from Reservation r
            where r.agentPromoteur.id = :agentId and coalesce(r.dateAttribution, r.dateReservation) >= :debut
            order by r.dateReservation desc
            """,
            countQuery = """
            select count(r) from Reservation r
            where r.agentPromoteur.id = :agentId and coalesce(r.dateAttribution, r.dateReservation) >= :debut
            """)
    org.springframework.data.domain.Page<Reservation> pageParAgent(@Param("agentId") Long agentId, @Param("debut") java.time.LocalDateTime debut,
                                                                   org.springframework.data.domain.Pageable pageable);

    List<Reservation> findByStatut(StatutReservation statut);

    /** Demandes en attente depuis au moins le délai autorisé et sans agent affecté. */
    /** Réservations dont le décompte est arrivé à échéance. */
    List<Reservation> findByStatutAndDateExpirationLessThanEqual(StatutReservation statut, java.time.LocalDateTime dateLimite);

    List<Reservation> findByBienFoncierIdAndStatutInAndIdNot(Long bienId, java.util.Collection<StatutReservation> statuts, Long id);

    long countByBienFoncierIdAndStatutIn(Long bienId, java.util.Collection<StatutReservation> statuts);

    boolean existsByAcquereurIdAndBienFoncierIdAndStatutIn(Long acquereurId, Long bienId, java.util.Collection<StatutReservation> statuts);

    boolean existsByBienFoncierIdAndStatutIn(Long bienId, java.util.Collection<StatutReservation> statuts);

    boolean existsByBienFoncierIdAndStatutAndIdNot(Long bienId, StatutReservation statut, Long id);

    List<Reservation> findBySuiteVenteAndDateLimiteChoixLessThanEqual(com.example.foncierback.entity.enums.SuiteVente suite, java.time.LocalDateTime date);

    List<Reservation> findBySuiteVenteAndDateRemboursementPrevueLessThanEqual(com.example.foncierback.entity.enums.SuiteVente suite, java.time.LocalDateTime date);

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
            LEFT JOIN maisons_cle_en_main mc ON mc.id = b.id
            WHERE pf.societe_id = :societeId
               OR pi.societe_id = :societeId
               OR mc.societe_id = :societeId
            ORDER BY r.date_reservation DESC
            """, nativeQuery = true)
    List<Reservation> findBySocieteId(@Param("societeId") Long societeId);
}
