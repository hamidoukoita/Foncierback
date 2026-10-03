package com.example.foncierback.repository;

import com.example.foncierback.entity.PlanMasseElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanMasseElementRepository extends JpaRepository<PlanMasseElement, Long> {
    List<PlanMasseElement> findByPlanMasseIdOrderByZIndexAscIdAsc(Long planMasseId);
}
