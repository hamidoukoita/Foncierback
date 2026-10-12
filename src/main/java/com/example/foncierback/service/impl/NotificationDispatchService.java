package com.example.foncierback.service.impl;

import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.Notification;
import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.TypeNotification;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.NotificationRepository;
import com.example.foncierback.repository.TypeNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralise la création des notifications métier pour éviter que chaque contrôleur
 * ait à fabriquer des notifications séparément.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationDispatchService {

    private final NotificationRepository notificationRepository;
    private final TypeNotificationRepository typeNotificationRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;

    public void notifyUser(Utilisateur utilisateur, String code, String libelle, String titre, String message) {
        if (utilisateur == null || utilisateur.getId() == null) return;
        Notification notification = Notification.builder()
                .titre(titre)
                .message(message)
                .lue(false)
                .dateEnvoi(LocalDateTime.now())
                .typeNotification(resolveType(code, libelle))
                .utilisateur(utilisateur)
                .build();
        notificationRepository.save(notification);
    }

    public void notifySociete(SocietePromotrice societe, String code, String libelle, String titre, String message) {
        if (societe == null || societe.getId() == null) return;
        List<AgentPromoteur> destinataires = agentPromoteurRepository.findBySocietePromotriceId(societe.getId());
        for (AgentPromoteur agent : destinataires) {
            StatutCompte statut = agent.getStatut();
            if (statut == StatutCompte.SUSPENDU || statut == StatutCompte.DESACTIVE) continue;
            notifyUser(agent, code, libelle, titre, message);
        }
    }

    public void notifySocietePourBien(BienFoncier bien, String code, String libelle, String titre, String message) {
        if (bien instanceof LotProgramme lot && lot.getProgrammeFoncier() != null) {
            notifySociete(lot.getProgrammeFoncier().getSocietePromotrice(), code, libelle, titre, message);
            return;
        }
        if (bien instanceof ParcelleIndividuelle parcelle) {
            notifySociete(parcelle.getSocietePromotrice(), code, libelle, titre, message);
        }
    }

    private TypeNotification resolveType(String code, String libelle) {
        return typeNotificationRepository.findByCode(code)
                .or(() -> typeNotificationRepository.findByLibelle(libelle))
                .orElseGet(() -> typeNotificationRepository.save(TypeNotification.builder()
                        .code(code)
                        .libelle(libelle)
                        .description("Notification générée automatiquement par le workflow métier Foncier+.")
                        .build()));
    }
}
