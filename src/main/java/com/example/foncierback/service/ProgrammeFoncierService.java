package com.example.foncierback.service;

import com.example.foncierback.dto.request.ProgrammeFoncierRequest;
import com.example.foncierback.dto.response.ProgrammeFoncierResponse;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.entity.enums.StatutProgramme;

import java.util.List;

public interface ProgrammeFoncierService {

    ProgrammeFoncierResponse create(ProgrammeFoncierRequest request);

    ProgrammeFoncierResponse getById(Long id);

    List<ProgrammeFoncierResponse> getAll();

    ProgrammeFoncierResponse update(Long id, ProgrammeFoncierRequest request);

    void delete(Long id);

    List<ProgrammeFoncierResponse> search(String keyword);

    List<ProgrammeFoncierResponse> filterByStatut(StatutProgramme statut);

    List<ProgrammeFoncierResponse> findBySociete(Long societeId);

    ProgrammeFoncier findEntityById(Long id);
}
