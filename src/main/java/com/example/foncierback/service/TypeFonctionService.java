package com.example.foncierback.service;

import com.example.foncierback.dto.request.TypeFonctionRequest;
import com.example.foncierback.dto.response.TypeFonctionResponse;
import com.example.foncierback.entity.TypeFonction;

import java.util.List;

public interface TypeFonctionService {

    TypeFonctionResponse create(TypeFonctionRequest request);

    TypeFonctionResponse getById(Long id);

    List<TypeFonctionResponse> getAll();

    List<TypeFonctionResponse> getByNiveauAccesId(Long niveauAccesId);

    TypeFonctionResponse update(Long id, TypeFonctionRequest request);

    void delete(Long id);

    TypeFonction findEntityById(Long id);
}
