package com.example.foncierback.repository;

import com.example.foncierback.entity.Acquereur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcquereurRepository extends JpaRepository<Acquereur, Long>, JpaSpecificationExecutor<Acquereur> {
    Optional<Acquereur> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
}
