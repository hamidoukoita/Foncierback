package com.example.foncierback.repository;

import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.enums.StatutParcelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BienFoncierRepository extends JpaRepository<BienFoncier, Long>, JpaSpecificationExecutor<BienFoncier> {

    Optional<BienFoncier> findByReference(String reference);

    boolean existsByReference(String reference);

    boolean existsByReferenceAndIdNot(String reference, Long id);

    List<BienFoncier> findByStatut(StatutParcelle statut);

    List<BienFoncier> findByPrixBetween(BigDecimal prixMin, BigDecimal prixMax);

    List<BienFoncier> findBySuperficieBetween(Double superficieMin, Double superficieMax);
}
