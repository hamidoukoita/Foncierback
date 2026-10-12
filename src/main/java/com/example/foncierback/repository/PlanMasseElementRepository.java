package com.example.foncierback.repository;

import com.example.foncierback.entity.PlanMasseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanMasseElementRepository extends JpaRepository<PlanMasseElement, Long> {

    // Requête explicite : le nom dérivé « OrderByZIndex » ne se résout pas vers l'attribut « zIndex » (Hibernate).
    @Query("SELECT p FROM PlanMasseElement p WHERE p.planMasse.id = :planMasseId ORDER BY p.zIndex ASC, p.id ASC")
    List<PlanMasseElement> findByPlanMasseIdOrderByZIndexAscIdAsc(@Param("planMasseId") Long planMasseId);
}
