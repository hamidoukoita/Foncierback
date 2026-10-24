package com.example.foncierback.repository;

import com.example.foncierback.entity.PropositionProjet;
import com.example.foncierback.entity.enums.StatutProposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropositionProjetRepository extends JpaRepository<PropositionProjet, Long> {

    List<PropositionProjet> findByProjetIdOrderByDatePropositionAsc(Long projetId);

    List<PropositionProjet> findByProjetIdAndStatut(Long projetId, StatutProposition statut);

    long countByProjetId(Long projetId);
}
