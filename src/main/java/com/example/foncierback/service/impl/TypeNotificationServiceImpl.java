package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.TypeNotificationRequest;
import com.example.foncierback.dto.response.TypeNotificationResponse;
import com.example.foncierback.entity.TypeNotification;
import com.example.foncierback.repository.TypeNotificationRepository;
import com.example.foncierback.service.TypeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TypeNotificationServiceImpl implements TypeNotificationService {

    private final TypeNotificationRepository typeNotificationRepository;

    @Override
    public TypeNotificationResponse create(TypeNotificationRequest request) {
        if (typeNotificationRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un type de notification avec le libellé '" + request.getLibelle() + "' existe déjà");
        }
        if (request.getCode() != null && !request.getCode().isBlank() && typeNotificationRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un type de notification avec le code '" + request.getCode() + "' existe déjà");
        }

        TypeNotification typeNotification = TypeNotification.builder()
                .libelle(request.getLibelle())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        TypeNotification saved = typeNotificationRepository.save(typeNotification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeNotificationResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeNotificationResponse> getAll() {
        return typeNotificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TypeNotificationResponse update(Long id, TypeNotificationRequest request) {
        TypeNotification existing = findEntityById(id);

        if (!existing.getLibelle().equalsIgnoreCase(request.getLibelle())
                && typeNotificationRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un type de notification avec le libellé '" + request.getLibelle() + "' existe déjà");
        }

        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (!request.getCode().equalsIgnoreCase(existing.getCode())
                    && typeNotificationRepository.existsByCode(request.getCode())) {
                throw new BadRequestException("Un type de notification avec le code '" + request.getCode() + "' existe déjà");
            }
            existing.setCode(request.getCode());
        } else {
            existing.setCode(null);
        }

        existing.setLibelle(request.getLibelle());
        existing.setDescription(request.getDescription());

        TypeNotification updated = typeNotificationRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        TypeNotification existing = findEntityById(id);
        typeNotificationRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeNotification findEntityById(Long id) {
        return typeNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de notification introuvable avec l'identifiant : " + id));
    }

    private TypeNotificationResponse mapToResponse(TypeNotification entity) {
        return TypeNotificationResponse.builder()
                .id(entity.getId())
                .libelle(entity.getLibelle())
                .code(entity.getCode())
                .description(entity.getDescription())
                .build();
    }
}
