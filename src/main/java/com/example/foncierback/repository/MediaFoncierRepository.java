package com.example.foncierback.repository;

import com.example.foncierback.entity.MediaFoncier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.example.foncierback.entity.enums.TypeMedia;
import java.util.Optional;

@Repository
public interface MediaFoncierRepository extends JpaRepository<MediaFoncier, Long> {

    List<MediaFoncier> findByProgrammeFoncierIdOrderByOrdreAscIdAsc(Long programmeId);

    List<MediaFoncier> findByBienFoncierIdOrderByOrdreAscIdAsc(Long bienId);

    Optional<MediaFoncier> findFirstByProgrammeFoncierIdAndTypeOrderByOrdreAscIdAsc(Long programmeId, TypeMedia type);

    Optional<MediaFoncier> findFirstByBienFoncierIdAndTypeOrderByOrdreAscIdAsc(Long bienId, TypeMedia type);

    long countByProgrammeFoncierIdAndType(Long programmeId, TypeMedia type);

    long countByBienFoncierIdAndType(Long bienId, TypeMedia type);

    @Query("select coalesce(max(m.ordre), 0) from MediaFoncier m where m.programmeFoncier.id = :programmeId")
    Integer maxOrdreProgramme(@Param("programmeId") Long programmeId);

    @Query("select coalesce(max(m.ordre), 0) from MediaFoncier m where m.bienFoncier.id = :bienId")
    Integer maxOrdreBien(@Param("bienId") Long bienId);
}
