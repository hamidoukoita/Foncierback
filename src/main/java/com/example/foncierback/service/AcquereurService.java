package com.example.foncierback.service;

import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.response.AcquereurResponse;
import com.example.foncierback.entity.Acquereur;

import java.util.List;

public interface AcquereurService {

    AcquereurResponse create(AcquereurRequest request);

    AcquereurResponse getById(Long id);

    List<AcquereurResponse> getAll();

    AcquereurResponse update(Long id, AcquereurRequest request);

    void delete(Long id);

    Acquereur findEntityById(Long id);
}
