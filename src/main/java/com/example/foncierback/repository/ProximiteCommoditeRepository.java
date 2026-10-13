package com.example.foncierback.repository;

import com.example.foncierback.entity.ProximiteCommodite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProximiteCommoditeRepository extends JpaRepository<ProximiteCommodite, Long> {

    List<ProximiteCommodite> findByCommoditeId(Long commoditeId);

    @Query("""
        SELECT p FROM ProximiteCommodite p
        LEFT JOIN FETCH p.commodite c
        LEFT JOIN FETCH c.typeCommodite
        LEFT JOIN FETCH c.societePromotrice
        LEFT JOIN FETCH p.programmeFoncier prog
        LEFT JOIN FETCH prog.societePromotrice
        LEFT JOIN FETCH p.bienFoncier
        WHERE c.societePromotrice.id = :societeId
           OR prog.societePromotrice.id = :societeId
        """)
    List<ProximiteCommodite> findBySocieteId(@Param("societeId") Long societeId);
}
