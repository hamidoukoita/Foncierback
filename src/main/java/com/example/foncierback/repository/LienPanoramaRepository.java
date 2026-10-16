package com.example.foncierback.repository;

import com.example.foncierback.entity.LienPanorama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LienPanoramaRepository extends JpaRepository<LienPanorama, Long> {

    List<LienPanorama> findBySourceBienFoncierIdOrderByIdAsc(Long bienId);

    boolean existsBySourceIdAndCibleId(Long sourceId, Long cibleId);
}
