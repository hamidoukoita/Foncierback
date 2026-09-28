package com.example.foncierback.repository;

import com.example.foncierback.entity.AgentPromoteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentPromoteurRepository extends JpaRepository<AgentPromoteur, Long>, JpaSpecificationExecutor<AgentPromoteur> {
    Optional<AgentPromoteur> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
    List<AgentPromoteur> findBySocietePromotriceId(Long societeId);
    List<AgentPromoteur> findByTypeFonctionId(Long typeFonctionId);
}
