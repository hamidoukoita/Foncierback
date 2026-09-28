package com.example.foncierback.service;

import com.example.foncierback.dto.request.AgentPromoteurRequest;
import com.example.foncierback.dto.response.AgentPromoteurResponse;
import com.example.foncierback.entity.AgentPromoteur;

import java.util.List;

public interface AgentPromoteurService {

    AgentPromoteurResponse create(AgentPromoteurRequest request);

    AgentPromoteurResponse getById(Long id);

    List<AgentPromoteurResponse> getAll();

    AgentPromoteurResponse update(Long id, AgentPromoteurRequest request);

    void delete(Long id);

    List<AgentPromoteurResponse> getBySocieteId(Long societeId);

    AgentPromoteur findEntityById(Long id);
}
