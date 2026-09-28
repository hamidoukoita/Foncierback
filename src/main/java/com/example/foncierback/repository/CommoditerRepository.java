package com.example.foncierback.repository;

import com.example.foncierback.entity.Commoditer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommoditerRepository extends JpaRepository<Commoditer, Long>, JpaSpecificationExecutor<Commoditer> {
    Optional<Commoditer> findByNom(String nom);
    boolean existsByNom(String nom);
    List<Commoditer> findByNomContainingIgnoreCase(String keyword);
    List<Commoditer> findByTypeCommoditeId(Long typeCommoditeId);
}
