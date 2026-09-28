package com.example.foncierback.repository;

import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.enums.StatutParcelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotProgrammeRepository extends JpaRepository<LotProgramme, Long>, JpaSpecificationExecutor<LotProgramme> {

    List<LotProgramme> findByProgrammeFoncierId(Long programmeFoncierId);

    List<LotProgramme> findByProgrammeFoncierIdAndStatut(Long programmeFoncierId, StatutParcelle statut);

    Optional<LotProgramme> findByProgrammeFoncierIdAndNumeroLot(Long programmeFoncierId, String numeroLot);

    boolean existsByProgrammeFoncierIdAndNumeroLot(Long programmeFoncierId, String numeroLot);

    boolean existsByProgrammeFoncierIdAndNumeroLotAndIdNot(Long programmeFoncierId, String numeroLot, Long id);

    long countByProgrammeFoncierId(Long programmeFoncierId);

    long countByProgrammeFoncierIdAndStatut(Long programmeFoncierId, StatutParcelle statut);
}
