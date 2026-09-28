package com.example.foncierback.repository;

import com.example.foncierback.entity.TypeCommoditer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeCommoditerRepository extends JpaRepository<TypeCommoditer, Long>, JpaSpecificationExecutor<TypeCommoditer> {

    Optional<TypeCommoditer> findByLibelle(String libelle);

    Optional<TypeCommoditer> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelle(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}
