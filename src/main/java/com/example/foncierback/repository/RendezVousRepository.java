package com.example.foncierback.repository;

import com.example.foncierback.entity.RendezVous;
import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.TypeRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long>, JpaSpecificationExecutor<RendezVous> {

    List<RendezVous> findByAcquereurId(Long acquereurId);

    /** Prochain rendez-vous encore valable d'un acquéreur pour un bien (demandé ou accepté, à venir). */
    java.util.Optional<RendezVous> findFirstByAcquereurIdAndBienFoncierIdAndStatutInAndDateRendezVousAfterOrderByDateRendezVousAsc(
            Long acquereurId, Long bienId, java.util.Collection<StatutRendezVous> statuts, LocalDateTime apres);

    List<RendezVous> findByAgentPromoteurId(Long agentId);

    long countByAgentPromoteurId(Long agentId);

    // --- Indicateurs d'un agent : visites dont la date tombe dans la période ---

    @Query("select v.statut, count(v) from RendezVous v where v.agentPromoteur.id = :agentId and v.dateRendezVous >= :debut group by v.statut")
    List<Object[]> compterParStatutPourAgent(@Param("agentId") Long agentId, @Param("debut") LocalDateTime debut);

    /** Visites acceptées dont la date est passée : effectuées. */
    @Query("""
            select count(v) from RendezVous v
            where v.agentPromoteur.id = :agentId and v.dateRendezVous >= :debut
              and v.statut = :acceptee and v.dateRendezVous < :maintenant
            """)
    long compterEffectueesPourAgent(@Param("agentId") Long agentId, @Param("debut") LocalDateTime debut,
                                    @Param("acceptee") StatutRendezVous acceptee, @Param("maintenant") LocalDateTime maintenant);

    @Query(value = "select v from RendezVous v where v.agentPromoteur.id = :agentId and v.dateRendezVous >= :debut order by v.dateRendezVous desc",
            countQuery = "select count(v) from RendezVous v where v.agentPromoteur.id = :agentId and v.dateRendezVous >= :debut")
    org.springframework.data.domain.Page<RendezVous> pageParAgent(@Param("agentId") Long agentId, @Param("debut") LocalDateTime debut,
                                                                  org.springframework.data.domain.Pageable pageable);

    List<RendezVous> findByBienFoncierId(Long bienId);

    List<RendezVous> findByCreneauId(Long creneauId);

    List<RendezVous> findByStatut(StatutRendezVous statut);

    List<RendezVous> findByTypeRDV(TypeRDV typeRDV);

    List<RendezVous> findByDateRendezVousBetween(LocalDateTime debut, LocalDateTime fin);

    List<RendezVous> findByAgentPromoteurIdAndDateRendezVousBetween(Long agentId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Rendez-vous relevant d'une société : par agent affecté ou par bien
     * rattaché à un programme/parcelle de cette société.
     */
    @Query(value = """
            SELECT rv.*
            FROM rendez_vous rv
            LEFT JOIN agents_promoteurs ap ON ap.id = rv.agent_id
            LEFT JOIN biens_fonciers b ON b.id = rv.bien_id
            LEFT JOIN lots_programmes lp ON lp.id = b.id
            LEFT JOIN programmes_fonciers pf ON pf.id = lp.programme_id
            LEFT JOIN parcelles_individuelles pi ON pi.id = b.id
            LEFT JOIN maisons_cle_en_main mc ON mc.id = b.id
            WHERE ap.societe_id = :societeId
               OR pf.societe_id = :societeId
               OR pi.societe_id = :societeId
               OR mc.societe_id = :societeId
            ORDER BY rv.date_rendez_vous ASC
            """, nativeQuery = true)
    List<RendezVous> findBySocieteId(@Param("societeId") Long societeId);
}
