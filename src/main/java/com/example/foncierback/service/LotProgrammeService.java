package com.example.foncierback.service;

import com.example.foncierback.dto.request.LotProgrammeRequest;
import com.example.foncierback.dto.response.LotProgrammeResponse;
import com.example.foncierback.entity.LotProgramme;

import java.util.List;

public interface LotProgrammeService {

    LotProgrammeResponse create(LotProgrammeRequest request);

    LotProgrammeResponse getById(Long id);

    List<LotProgrammeResponse> getAll();

    LotProgrammeResponse update(Long id, LotProgrammeRequest request);

    void delete(Long id);

    List<LotProgrammeResponse> getByProgrammeId(Long programmeId);

    LotProgramme findEntityById(Long id);
}
