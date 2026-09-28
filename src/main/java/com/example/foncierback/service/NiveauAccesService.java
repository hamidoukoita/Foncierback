package com.example.foncierback.service;

import com.example.foncierback.dto.request.NiveauAccesRequest;
import com.example.foncierback.dto.response.NiveauAccesResponse;
import com.example.foncierback.entity.NiveauAcces;

import java.util.List;

public interface NiveauAccesService {

    NiveauAccesResponse create(NiveauAccesRequest request);

    NiveauAccesResponse getById(Long id);

    List<NiveauAccesResponse> getAll();

    NiveauAccesResponse update(Long id, NiveauAccesRequest request);

    void delete(Long id);

    NiveauAcces findEntityById(Long id);
}
