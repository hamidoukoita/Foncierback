package com.example.foncierback.repository;

import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocietePromotriceRepository extends JpaRepository<SocietePromotrice, Long>, JpaSpecificationExecutor<SocietePromotrice> {

    Optional<SocietePromotrice> findByNom(String nom);

    Optional<SocietePromotrice> findByEmail(String email);

    Optional<SocietePromotrice> findByTelephone(String telephone);

    Optional<SocietePromotrice> findByNif(String nif);

    Optional<SocietePromotrice> findByNumeroAgrement(String numeroAgrement);

    boolean existsByNom(String nom);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    boolean existsByNif(String nif);

    boolean existsByNumeroAgrement(String numeroAgrement);

    List<SocietePromotrice> findByStatutAgrement(StatutAgrement statutAgrement);
}
