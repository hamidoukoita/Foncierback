package com.example.foncierback.repository;

import com.example.foncierback.entity.ModelMaison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
