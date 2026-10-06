package com.example.foncierback.repository;

import com.example.foncierback.entity.ModelMaison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelMaisonRepository extends JpaRepository<ModelMaison, Long>, JpaSpecificationExecutor<ModelMaison> {

    Optional<ModelMaison> findByLibeller(String libeller);
    Optional<ModelMaison> findByLibellerIgnoreCase(String libeller);
    boolean existsByLibeller(String libeller);
    boolean existsByLibellerIgnoreCase(String libeller);
    List<ModelMaison> findByLibellerContainingIgnoreCase(String keyword);

    @Query("""
        select m from ModelMaison m
        where (:societeId is null or m.societePromotrice.id = :societeId or m.societePromotrice is null)
          and (:surface is null or (m.surfaceTerrainMin is null or m.surfaceTerrainMin <= :surface))
          and (:surface is null or (m.surfaceTerrainMax is null or m.surfaceTerrainMax >= :surface))
          and (:typeTerrain is null or :typeTerrain = '' or m.typeTerrainCompatible is null or upper(m.typeTerrainCompatible) = upper(:typeTerrain))
        order by m.libeller asc
        """)
    List<ModelMaison> findCompatibles(@Param("surface") Double surface, @Param("typeTerrain") String typeTerrain, @Param("societeId") Long societeId);

    List<ModelMaison> findBySocietePromotriceIdOrSocietePromotriceIsNullOrderByLibellerAsc(Long societeId);
}
