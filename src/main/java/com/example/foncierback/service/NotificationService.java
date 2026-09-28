package com.example.foncierback.service;

import com.example.foncierback.dto.request.NotificationRequest;
import com.example.foncierback.dto.response.NotificationResponse;
import com.example.foncierback.entity.Notification;

import java.util.List;

public interface NotificationService {

    NotificationResponse create(NotificationRequest request);

    NotificationResponse getById(Long id);

    List<NotificationResponse> getAll();

    List<NotificationResponse> getByUtilisateurId(Long utilisateurId);

    NotificationResponse markAsRead(Long id);

    void delete(Long id);

    Notification findEntityById(Long id);
}
