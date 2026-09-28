package com.example.foncierback.service;

import com.example.foncierback.dto.request.ModelMaisonRequest;
import com.example.foncierback.dto.response.ModelMaisonResponse;
import com.example.foncierback.entity.ModelMaison;

import java.util.List;

public interface ModelMaisonService {

    ModelMaisonResponse create(ModelMaisonRequest request);

    ModelMaisonResponse getById(Long id);

    List<ModelMaisonResponse> getAll();

    ModelMaisonResponse update(Long id, ModelMaisonRequest request);

    void delete(Long id);

    List<ModelMaisonResponse> searchByLibeller(String keyword);

    ModelMaison findEntityById(Long id);
}
