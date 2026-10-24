package com.example.foncierback.repository;

import com.example.foncierback.entity.MessageProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageProjetRepository extends JpaRepository<MessageProjet, Long> {

    List<MessageProjet> findByProjetIdOrderByDateEnvoiAsc(Long projetId);
}
