package com.example.foncierback.repository;

import com.example.foncierback.entity.ProximiteCommodite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProximiteCommoditeRepository extends JpaRepository<ProximiteCommodite, Long> {

    List<ProximiteCommodite> findByProgrammeFoncierId(Long programmeId);

    List<ProximiteCommodite> findByBienFoncierId(Long bienId);

    @Query("SELECT p FROM ProximiteCommodite p WHERE p.commodite.societePromotrice.id = :societeId")
    List<ProximiteCommodite> findBySocieteId(@Param("societeId") Long societeId);

    boolean existsByCommoditeIdAndProgrammeFoncierId(Long commoditeId, Long programmeId);

    boolean existsByCommoditeIdAndBienFoncierId(Long commoditeId, Long bienId);

    void deleteByCommoditeId(Long commoditeId);
}
