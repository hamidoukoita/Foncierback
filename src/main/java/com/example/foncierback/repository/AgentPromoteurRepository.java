package com.example.foncierback.repository;

import com.example.foncierback.entity.AgentPromoteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentPromoteurRepository extends JpaRepository<AgentPromoteur, Long> {

    List<AgentPromoteur> findBySocietePromotriceId(Long societeId);
}