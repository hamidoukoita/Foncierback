package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.NotificationRequest;
import com.example.foncierback.dto.response.NotificationResponse;
import com.example.foncierback.entity.Notification;
import com.example.foncierback.entity.TypeNotification;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.repository.NotificationRepository;
import com.example.foncierback.repository.TypeNotificationRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TypeNotificationRepository typeNotificationRepository;

    @Override
    public NotificationResponse create(NotificationRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'identifiant : " + request.getUtilisateurId()));

        TypeNotification typeNotification = null;
        if (request.getTypeNotificationId() != null) {
            typeNotification = typeNotificationRepository.findById(request.getTypeNotificationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de notification introuvable avec l'identifiant : " + request.getTypeNotificationId()));
        }

        Notification notification = Notification.builder()
                .titre(request.getTitre())
                .message(request.getMessage())
                .lue(false)
                .dateEnvoi(LocalDateTime.now())
                .typeNotification(typeNotification)
                .utilisateur(utilisateur)
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUtilisateurId(Long utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new ResourceNotFoundException("Utilisateur introuvable avec l'identifiant : " + utilisateurId);
        }
        return notificationRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification notification = findEntityById(id);
        notification.setLue(true);
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Notification notification = findEntityById(id);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable avec l'identifiant : " + id));
    }

    private NotificationResponse mapToResponse(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .titre(entity.getTitre())
                .message(entity.getMessage())
                .lue(entity.getLue())
                .dateEnvoi(entity.getDateEnvoi())
                .typeNotificationId(entity.getTypeNotification() != null ? entity.getTypeNotification().getId() : null)
                .typeNotificationLibelle(entity.getTypeNotification() != null ? entity.getTypeNotification().getLibelle() : null)
                .utilisateurId(entity.getUtilisateur() != null ? entity.getUtilisateur().getId() : null)
                .utilisateurNom(entity.getUtilisateur() != null ? entity.getUtilisateur().getNom() : null)
                .utilisateurPrenom(entity.getUtilisateur() != null ? entity.getUtilisateur().getPrenom() : null)
                .utilisateurTelephone(entity.getUtilisateur() != null ? entity.getUtilisateur().getTelephone() : null)
                .build();
    }
}
