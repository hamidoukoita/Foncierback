package com.example.foncierback.repository;

import com.example.foncierback.entity.DocumentKyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentKycRepository extends JpaRepository<DocumentKyc, Long> {
    List<DocumentKyc> findBySocietePromotriceId(Long societeId);
}
