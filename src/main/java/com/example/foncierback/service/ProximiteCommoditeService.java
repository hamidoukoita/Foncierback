package com.example.foncierback.service;

import com.example.foncierback.dto.request.ProximiteCommoditeRequest;
import com.example.foncierback.dto.response.ProximiteCommoditeResponse;

import java.util.List;

public interface ProximiteCommoditeService {

    ProximiteCommoditeResponse create(ProximiteCommoditeRequest request);

    ProximiteCommoditeResponse update(Long id, ProximiteCommoditeRequest request);

    void delete(Long id);

    ProximiteCommoditeResponse getById(Long id);

    List<ProximiteCommoditeResponse> findByProgramme(Long programmeId);

    List<ProximiteCommoditeResponse> findByBien(Long bienId);

    List<ProximiteCommoditeResponse> findBySociete(Long societeId);
}
