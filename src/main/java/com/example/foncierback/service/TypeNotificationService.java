package com.example.foncierback.service;

import com.example.foncierback.dto.request.TypeNotificationRequest;
import com.example.foncierback.dto.response.TypeNotificationResponse;
import com.example.foncierback.entity.TypeNotification;

import java.util.List;

public interface TypeNotificationService {

    TypeNotificationResponse create(TypeNotificationRequest request);

    TypeNotificationResponse getById(Long id);

    List<TypeNotificationResponse> getAll();

    TypeNotificationResponse update(Long id, TypeNotificationRequest request);

    void delete(Long id);

    TypeNotification findEntityById(Long id);
}
