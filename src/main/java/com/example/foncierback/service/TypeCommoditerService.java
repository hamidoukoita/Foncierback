package com.example.foncierback.service;

import com.example.foncierback.dto.request.TypeCommoditerRequest;
import com.example.foncierback.dto.response.TypeCommoditerResponse;
import com.example.foncierback.entity.TypeCommoditer;

import java.util.List;

public interface TypeCommoditerService {

    TypeCommoditerResponse create(TypeCommoditerRequest request);

    TypeCommoditerResponse getById(Long id);

    List<TypeCommoditerResponse> getAll();

    TypeCommoditerResponse update(Long id, TypeCommoditerRequest request);

    void delete(Long id);

    TypeCommoditer findEntityById(Long id);
}
