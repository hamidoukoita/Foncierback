package com.example.foncierback.repository;

import com.example.foncierback.entity.MaisonCleEnMain;
import com.example.foncierback.entity.enums.StatutAgrement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaisonCleEnMainRepository extends JpaRepository<MaisonCleEnMain, Long> {

    List<MaisonCleEnMain> findBySocietePromotriceIdOrderByIdDesc(Long societeId);

    List<MaisonCleEnMain> findBySocietePromotriceIdAndStatutIn(Long societeId, java.util.Collection<com.example.foncierback.entity.enums.StatutParcelle> statuts);

    /** Catalogue public : seules les sociétés agréées publient (règle « aucune publication sans agrément »). */
    List<MaisonCleEnMain> findBySocietePromotriceStatutAgrementOrderByIdDesc(StatutAgrement statut);
}
