package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ModelMaisonRequest;
import com.example.foncierback.dto.response.ModelMaisonResponse;
import com.example.foncierback.entity.ModelMaison;
import com.example.foncierback.repository.ModelMaisonRepository;
import com.example.foncierback.service.ModelMaisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelMaisonServiceImpl implements ModelMaisonService {

    private final ModelMaisonRepository modelMaisonRepository;

    @Override
    public ModelMaisonResponse create(ModelMaisonRequest request) {
        if (modelMaisonRepository.existsByLibellerIgnoreCase(request.getLibeller())) {
            throw new BadRequestException("Un modèle de maison avec le libellé '" + request.getLibeller() + "' existe déjà");
        }

        ModelMaison modelMaison = ModelMaison.builder()
                .libeller(request.getLibeller())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        ModelMaison saved = modelMaisonRepository.save(modelMaison);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMaisonResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMaisonResponse> getAll() {
        return modelMaisonRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ModelMaisonResponse update(Long id, ModelMaisonRequest request) {
        ModelMaison existing = findEntityById(id);

        if (!existing.getLibeller().equalsIgnoreCase(request.getLibeller())
                && modelMaisonRepository.existsByLibellerIgnoreCase(request.getLibeller())) {
            throw new BadRequestException("Un modèle de maison avec le libellé '" + request.getLibeller() + "' existe déjà");
        }

        existing.setLibeller(request.getLibeller());
        existing.setDescription(request.getDescription());
        existing.setImageUrl(request.getImageUrl());

        ModelMaison updated = modelMaisonRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        ModelMaison existing = findEntityById(id);
        modelMaisonRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMaisonResponse> searchByLibeller(String keyword) {
        return modelMaisonRepository.findByLibellerContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMaison findEntityById(Long id) {
        return modelMaisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + id));
    }

    private ModelMaisonResponse mapToResponse(ModelMaison entity) {
        return ModelMaisonResponse.builder()
                .id(entity.getId())
                .libeller(entity.getLibeller())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
