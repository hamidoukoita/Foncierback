package com.example.foncierback.service;

import com.example.foncierback.dto.request.ProjetConstructionRequest;
import com.example.foncierback.dto.response.ProjetConstructionResponse;
import com.example.foncierback.entity.ProjetConstruction;

import java.util.List;

public interface ProjetConstructionService {

    ProjetConstructionResponse create(ProjetConstructionRequest request);

    ProjetConstructionResponse getById(Long id);

    List<ProjetConstructionResponse> getAll();

    ProjetConstructionResponse update(Long id, ProjetConstructionRequest request);

    void delete(Long id);

    ProjetConstructionResponse valider(Long id);

    ProjetConstructionResponse refuser(Long id, String motifRefus);

    List<ProjetConstructionResponse> findByAcquereur(Long acquereurId);

    List<ProjetConstructionResponse> findBySociete(Long societeId);

    ProjetConstruction findEntityById(Long id);
}
