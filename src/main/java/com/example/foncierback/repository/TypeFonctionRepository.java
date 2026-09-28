package com.example.foncierback.repository;

import com.example.foncierback.entity.TypeFonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeFonctionRepository extends JpaRepository<TypeFonction, Long>, JpaSpecificationExecutor<TypeFonction> {

    Optional<TypeFonction> findByLibelle(String libelle);

    Optional<TypeFonction> findByCode(String code);

    List<TypeFonction> findByNiveauAccesId(Long niveauAccesId);

    boolean existsByLibelle(String libelle);

    boolean existsByCode(String code);
}
