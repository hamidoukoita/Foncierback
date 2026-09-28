package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.SocietePromotriceRequest;
import com.example.foncierback.dto.response.SocietePromotriceResponse;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.SocietePromotriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SocietePromotriceServiceImpl implements SocietePromotriceService {

    private final SocietePromotriceRepository societePromotriceRepository;

    @Override
    public SocietePromotriceResponse create(SocietePromotriceRequest request) {
        if (societePromotriceRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une société promotrice avec le nom '" + request.getNom() + "' existe déjà");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && societePromotriceRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Une société promotrice avec l'email '" + request.getEmail() + "' existe déjà");
        }
        if (request.getNif() != null && !request.getNif().isBlank()
                && societePromotriceRepository.existsByNif(request.getNif())) {
            throw new BadRequestException("Une société promotrice avec le NIF '" + request.getNif() + "' existe déjà");
        }
        if (request.getNumeroAgrement() != null && !request.getNumeroAgrement().isBlank()
                && societePromotriceRepository.existsByNumeroAgrement(request.getNumeroAgrement())) {
            throw new BadRequestException("Une société promotrice avec le numéro d'agrément '" + request.getNumeroAgrement() + "' existe déjà");
        }

        SocietePromotrice entity = SocietePromotrice.builder()
                .nom(request.getNom())
                .adresse(request.getAdresse())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .nif(request.getNif())
                .numeroAgrement(request.getNumeroAgrement())
                .dateAgrement(request.getDateAgrement())
                .statutAgrement(request.getStatutAgrement() != null ? request.getStatutAgrement() : StatutAgrement.EN_ATTENTE)
                .logoUrl(request.getLogoUrl())
                .siteWeb(request.getSiteWeb())
                .description(request.getDescription())
                .build();

        SocietePromotrice saved = societePromotriceRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietePromotriceResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietePromotriceResponse> getAll() {
        return societePromotriceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietePromotriceResponse> getByStatutAgrement(StatutAgrement statutAgrement) {
        return societePromotriceRepository.findByStatutAgrement(statutAgrement)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SocietePromotriceResponse update(Long id, SocietePromotriceRequest request) {
        SocietePromotrice existing = findEntityById(id);

        if (!existing.getNom().equalsIgnoreCase(request.getNom())
                && societePromotriceRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une société promotrice avec le nom '" + request.getNom() + "' existe déjà");
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(existing.getEmail())
                && societePromotriceRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Une société promotrice avec l'email '" + request.getEmail() + "' existe déjà");
        }

        if (request.getNif() != null && !request.getNif().equalsIgnoreCase(existing.getNif())
                && societePromotriceRepository.existsByNif(request.getNif())) {
            throw new BadRequestException("Une société promotrice avec le NIF '" + request.getNif() + "' existe déjà");
        }

        if (request.getNumeroAgrement() != null && !request.getNumeroAgrement().equalsIgnoreCase(existing.getNumeroAgrement())
                && societePromotriceRepository.existsByNumeroAgrement(request.getNumeroAgrement())) {
            throw new BadRequestException("Une société promotrice avec le numéro d'agrément '" + request.getNumeroAgrement() + "' existe déjà");
        }

        existing.setNom(request.getNom());
        existing.setAdresse(request.getAdresse());
        existing.setTelephone(request.getTelephone());
        existing.setEmail(request.getEmail());
        existing.setNif(request.getNif());
        existing.setNumeroAgrement(request.getNumeroAgrement());
        existing.setDateAgrement(request.getDateAgrement());
        if (request.getStatutAgrement() != null) {
            existing.setStatutAgrement(request.getStatutAgrement());
        }
        existing.setLogoUrl(request.getLogoUrl());
        existing.setSiteWeb(request.getSiteWeb());
        existing.setDescription(request.getDescription());

        SocietePromotrice updated = societePromotriceRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        SocietePromotrice existing = findEntityById(id);
        societePromotriceRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietePromotrice findEntityById(Long id) {
        return societePromotriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + id));
    }

    private SocietePromotriceResponse mapToResponse(SocietePromotrice entity) {
        return SocietePromotriceResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .adresse(entity.getAdresse())
                .telephone(entity.getTelephone())
                .email(entity.getEmail())
                .nif(entity.getNif())
                .numeroAgrement(entity.getNumeroAgrement())
                .dateAgrement(entity.getDateAgrement())
                .statutAgrement(entity.getStatutAgrement())
                .logoUrl(entity.getLogoUrl())
                .siteWeb(entity.getSiteWeb())
                .description(entity.getDescription())
                .build();
    }
}
