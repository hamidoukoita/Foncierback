package com.example.foncierback.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.foncierback.dto.request.ReglesReservationRequest;
import com.example.foncierback.dto.request.ReglesConstructionRequest;
import com.example.foncierback.dto.request.SocietePromotriceRequest;
import com.example.foncierback.dto.response.SocietePromotriceResponse;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;

import java.util.List;

public interface SocietePromotriceService {

    SocietePromotriceResponse create(SocietePromotriceRequest request);

    SocietePromotriceResponse getById(Long id);

    List<SocietePromotriceResponse> getAll();

    List<SocietePromotriceResponse> getByStatutAgrement(StatutAgrement statutAgrement);

    SocietePromotriceResponse update(Long id, SocietePromotriceRequest request);

    SocietePromotriceResponse updateReglesReservation(Long id, ReglesReservationRequest request);

    SocietePromotriceResponse updateReglesConstruction(Long id, ReglesConstructionRequest request);

    /** Remplace le logo de la société (image affichée sur ses publications). */
    SocietePromotriceResponse updateLogo(Long id, MultipartFile fichier);

    /** Marque l'assistant de première connexion comme terminé (ou passé). */
    SocietePromotriceResponse terminerParametrageInitial(Long id);

    void delete(Long id);

    SocietePromotrice findEntityById(Long id);
}
