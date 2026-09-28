package com.example.foncierback.repository;

import com.example.foncierback.entity.PlanMasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanMasseRepository extends JpaRepository<PlanMasse, Long> {

    Optional<PlanMasse> findByProgrammeFoncierId(Long programmeFoncierId);

    boolean existsByProgrammeFoncierId(Long programmeFoncierId);

    void deleteByProgrammeFoncierId(Long programmeFoncierId);
}
