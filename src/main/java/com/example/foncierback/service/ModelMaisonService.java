package com.example.foncierback.service;

import com.example.foncierback.dto.request.ModelMaisonRequest;
import com.example.foncierback.dto.response.ModelMaisonResponse;
import com.example.foncierback.entity.ModelMaison;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModelMaisonService {

    ModelMaisonResponse create(ModelMaisonRequest request);
    ModelMaisonResponse getById(Long id);
    List<ModelMaisonResponse> getAll(Long societeId);
    ModelMaisonResponse update(Long id, ModelMaisonRequest request);
    void delete(Long id);
    List<ModelMaisonResponse> searchByLibeller(String keyword);
    List<ModelMaisonResponse> findCompatibles(Double surfaceTerrain, String typeTerrain, Long societeId);
    ModelMaisonResponse uploadImage(Long id, MultipartFile file);
    ModelMaisonResponse uploadPlan(Long id, MultipartFile file);
    ModelMaison findEntityById(Long id);
}
