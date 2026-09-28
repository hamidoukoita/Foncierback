package com.example.foncierback.service;

import com.example.foncierback.dto.request.AdministrateurRequest;
import com.example.foncierback.dto.response.AdministrateurResponse;
import com.example.foncierback.entity.Administrateur;

import java.util.List;

public interface AdministrateurService {

    AdministrateurResponse create(AdministrateurRequest request);

    AdministrateurResponse getById(Long id);

    List<AdministrateurResponse> getAll();

    AdministrateurResponse update(Long id, AdministrateurRequest request);

    void delete(Long id);

    Administrateur findEntityById(Long id);
}
