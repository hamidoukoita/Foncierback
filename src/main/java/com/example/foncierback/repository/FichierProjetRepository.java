package com.example.foncierback.repository;

import com.example.foncierback.entity.FichierProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichierProjetRepository extends JpaRepository<FichierProjet, Long> {

    List<FichierProjet> findByProjetIdOrderByDateAjoutAsc(Long projetId);

    Optional<FichierProjet> findByIdAndProjetId(Long id, Long projetId);

    /** Pièces du dossier de l'acquéreur (hors plans joints aux propositions). */
    long countByProjetIdAndPropositionIsNull(Long projetId);
}
