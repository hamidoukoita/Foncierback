package com.example.foncierback.repository;

import com.example.foncierback.entity.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    List<Creneau> findBySocietePromotriceId(Long societeId);

    List<Creneau> findBySocietePromotriceIdAndDisponible(Long societeId, Boolean disponible);
}
