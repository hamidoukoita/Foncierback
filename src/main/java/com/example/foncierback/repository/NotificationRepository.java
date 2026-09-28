package com.example.foncierback.repository;

import com.example.foncierback.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUtilisateurId(Long utilisateurId);
    List<Notification> findByUtilisateurIdAndLue(Long utilisateurId, Boolean lue);
    long countByUtilisateurIdAndLueFalse(Long utilisateurId);
}
