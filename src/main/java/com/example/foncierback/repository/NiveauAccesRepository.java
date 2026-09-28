package com.example.foncierback.repository;

import com.example.foncierback.entity.NiveauAcces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NiveauAccesRepository extends JpaRepository<NiveauAcces, Long>, JpaSpecificationExecutor<NiveauAcces> {

    Optional<NiveauAcces> findByLibelle(String libelle);

    Optional<NiveauAcces> findByCode(String code);

    boolean existsByLibelle(String libelle);

    boolean existsByCode(String code);
}
