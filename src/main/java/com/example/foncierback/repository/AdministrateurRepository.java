package com.example.foncierback.repository;

import com.example.foncierback.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long>, JpaSpecificationExecutor<Administrateur> {
    Optional<Administrateur> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
}
