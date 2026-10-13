package com.example.foncierback.service;

import com.example.foncierback.dto.request.ProximiteCommoditeRequest;
import com.example.foncierback.dto.response.ProximiteCommoditeResponse;

import java.util.List;

public interface ProximiteCommoditeService {

    ProximiteCommoditeResponse create(ProximiteCommoditeRequest request);

    ProximiteCommoditeResponse getById(Long id);

    List<ProximiteCommoditeResponse> getBySocieteId(Long societeId);

    ProximiteCommoditeResponse update(Long id, ProximiteCommoditeRequest request);

    void delete(Long id);
}
