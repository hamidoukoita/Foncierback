package com.example.foncierback.service;

import com.example.foncierback.dto.request.CreneauRequest;
import com.example.foncierback.dto.response.CreneauResponse;
import com.example.foncierback.entity.Creneau;

import java.util.List;

public interface CreneauService {

    CreneauResponse create(CreneauRequest request);

    CreneauResponse getById(Long id);

    List<CreneauResponse> getAll();

    List<CreneauResponse> getBySocieteId(Long societeId);

    List<CreneauResponse> getBySocieteIdAndDisponible(Long societeId, Boolean disponible);

    CreneauResponse update(Long id, CreneauRequest request);

    void delete(Long id);

    Creneau findEntityById(Long id);
}
