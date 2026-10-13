package com.example.foncierback.service;

import com.example.foncierback.dto.request.CommoditerRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.entity.Commoditer;

import java.util.List;

public interface CommoditerService {

    CommoditerResponse create(CommoditerRequest request);

    CommoditerResponse getById(Long id);

    List<CommoditerResponse> getAll();

    List<CommoditerResponse> getByTypeCommoditeId(Long typeCommoditeId);

    /** Commodités référencées par une société promotrice. */
    List<CommoditerResponse> getBySociete(Long societeId);

    CommoditerResponse toResponse(Commoditer commoditer);

    List<CommoditerResponse> searchByNom(String keyword);

    CommoditerResponse update(Long id, CommoditerRequest request);

    void delete(Long id);

    Commoditer findEntityById(Long id);
}
