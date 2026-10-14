package com.example.foncierback.service;

import com.example.foncierback.dto.request.MaisonCleEnMainRequest;
import com.example.foncierback.dto.response.MaisonCleEnMainResponse;
import com.example.foncierback.entity.MaisonCleEnMain;

import java.util.List;

public interface MaisonCleEnMainService {
    MaisonCleEnMainResponse create(MaisonCleEnMainRequest request);
    MaisonCleEnMainResponse update(Long id, MaisonCleEnMainRequest request);
    void delete(Long id);
    MaisonCleEnMainResponse getById(Long id);
    /** Catalogue public : maisons des sociétés agréées. */
    List<MaisonCleEnMainResponse> getCatalogue();
    List<MaisonCleEnMainResponse> getBySociete(Long societeId);
    MaisonCleEnMain findEntityById(Long id);
}
