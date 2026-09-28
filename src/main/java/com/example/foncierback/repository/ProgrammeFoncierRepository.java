package com.example.foncierback.repository;

import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.entity.enums.StatutProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgrammeFoncierRepository extends JpaRepository<ProgrammeFoncier, Long>, JpaSpecificationExecutor<ProgrammeFoncier> {

    Optional<ProgrammeFoncier> findByNom(String nom);

    boolean existsByNom(String nom);

    Optional<ProgrammeFoncier> findByNumeroTitreMere(String numeroTitreMere);

    boolean existsByNumeroTitreMere(String numeroTitreMere);

    List<ProgrammeFoncier> findByStatut(StatutProgramme statut);

    List<ProgrammeFoncier> findBySocietePromotriceId(Long societeId);

    List<ProgrammeFoncier> findBySocietePromotriceIdAndStatut(Long societeId, StatutProgramme statut);

    List<ProgrammeFoncier> findByLieuContainingIgnoreCase(String lieu);
}
