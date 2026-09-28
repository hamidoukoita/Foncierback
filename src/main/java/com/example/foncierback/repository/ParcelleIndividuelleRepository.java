package com.example.foncierback.repository;

import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.enums.StatutParcelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelleIndividuelleRepository extends JpaRepository<ParcelleIndividuelle, Long>, JpaSpecificationExecutor<ParcelleIndividuelle> {

    Optional<ParcelleIndividuelle> findByNumeroTitreFoncier(String numeroTitreFoncier);

    boolean existsByNumeroTitreFoncier(String numeroTitreFoncier);

    List<ParcelleIndividuelle> findBySocietePromotriceId(Long societeId);

    List<ParcelleIndividuelle> findBySocietePromotriceIdAndStatut(Long societeId, StatutParcelle statut);
}
