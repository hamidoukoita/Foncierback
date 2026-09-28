package com.example.foncierback.service;

import com.example.foncierback.dto.request.ParcelleIndividuelleRequest;
import com.example.foncierback.dto.response.ParcelleIndividuelleResponse;
import com.example.foncierback.entity.ParcelleIndividuelle;

import java.util.List;

public interface ParcelleIndividuelleService {

    ParcelleIndividuelleResponse create(ParcelleIndividuelleRequest request);

    ParcelleIndividuelleResponse getById(Long id);

    List<ParcelleIndividuelleResponse> getAll();

    ParcelleIndividuelleResponse update(Long id, ParcelleIndividuelleRequest request);

    void delete(Long id);

    List<ParcelleIndividuelleResponse> getBySocieteId(Long societeId);

    ParcelleIndividuelle findEntityById(Long id);
}
