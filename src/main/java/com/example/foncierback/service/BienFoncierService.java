package com.example.foncierback.service;

import com.example.foncierback.dto.response.BienFoncierResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.enums.StatutParcelle;

import java.util.List;

public interface BienFoncierService {

    BienFoncierResponse getById(Long id);

    List<BienFoncierResponse> getAll();

    List<BienFoncierResponse> getByStatut(StatutParcelle statut);

    /**
     * Autorise (true) ou interdit (false) plusieurs réservations actives simultanées sur le bien.
     */
    BienFoncierResponse updateReservationMultiple(Long id, boolean autorise);

    BienFoncier findEntityById(Long id);
}
