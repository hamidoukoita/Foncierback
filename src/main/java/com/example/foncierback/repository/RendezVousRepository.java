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

    List<RendezVous> findByAgentPromoteurId(Long agentId);

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
            WHERE ap.societe_id = :societeId
               OR pf.societe_id = :societeId
               OR pi.societe_id = :societeId
            ORDER BY rv.date_rendez_vous ASC
            """, nativeQuery = true)
    List<RendezVous> findBySocieteId(@Param("societeId") Long societeId);
}
