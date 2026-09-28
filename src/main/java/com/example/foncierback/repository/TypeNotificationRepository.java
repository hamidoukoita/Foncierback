package com.example.foncierback.repository;

import com.example.foncierback.entity.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeNotificationRepository extends JpaRepository<TypeNotification, Long>, JpaSpecificationExecutor<TypeNotification> {
    Optional<TypeNotification> findByLibelle(String libelle);
    Optional<TypeNotification> findByCode(String code);
    boolean existsByLibelle(String libelle);
    boolean existsByCode(String code);
}
