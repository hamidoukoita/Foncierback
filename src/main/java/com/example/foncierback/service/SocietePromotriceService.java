package com.example.foncierback.service;

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

    void delete(Long id);

    SocietePromotrice findEntityById(Long id);
}
