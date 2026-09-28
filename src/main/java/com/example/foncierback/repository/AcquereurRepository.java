package com.example.foncierback.repository;

import com.example.foncierback.entity.Acquereur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcquereurRepository extends JpaRepository<Acquereur, Long> {

    Optional<Acquereur> findByTelephone(String telephone);

    boolean existsByTelephone(String telephone);
}