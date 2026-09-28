package com.example.foncierback.repository;

import com.example.foncierback.entity.ProjetConstruction;
import com.example.foncierback.entity.enums.StatutProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetConstructionRepository extends JpaRepository<ProjetConstruction, Long>, JpaSpecificationExecutor<ProjetConstruction> {

    Optional<ProjetConstruction> findByNumeroDossier(String numeroDossier);

    boolean existsByNumeroDossier(String numeroDossier);

    List<ProjetConstruction> findByAcquereurId(Long acquereurId);

    List<ProjetConstruction> findBySocietePromotriceId(Long societeId);

    List<ProjetConstruction> findBySocietePromotriceIdAndStatut(Long societeId, StatutProjet statut);

    List<ProjetConstruction> findByAgentPromoteurId(Long agentId);

    List<ProjetConstruction> findByStatut(StatutProjet statut);
}
