package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.TypeCommoditerRequest;
import com.example.foncierback.dto.response.TypeCommoditerResponse;
import com.example.foncierback.entity.TypeCommoditer;
import com.example.foncierback.repository.TypeCommoditerRepository;
import com.example.foncierback.service.TypeCommoditerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TypeCommoditerServiceImpl implements TypeCommoditerService {

    private final TypeCommoditerRepository typeCommoditerRepository;

    @Override
    public TypeCommoditerResponse create(TypeCommoditerRequest request) {
        if (typeCommoditerRepository.existsByLibelleIgnoreCase(request.getLibelle())) {
            throw new BadRequestException("Un type de commodité avec le libellé '" + request.getLibelle() + "' existe déjà");
        }

        TypeCommoditer typeCommoditer = TypeCommoditer.builder()
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .build();

        TypeCommoditer saved = typeCommoditerRepository.save(typeCommoditer);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeCommoditerResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeCommoditerResponse> getAll() {
        return typeCommoditerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TypeCommoditerResponse update(Long id, TypeCommoditerRequest request) {
        TypeCommoditer existing = findEntityById(id);

        if (!existing.getLibelle().equalsIgnoreCase(request.getLibelle())
                && typeCommoditerRepository.existsByLibelleIgnoreCase(request.getLibelle())) {
            throw new BadRequestException("Un type de commodité avec le libellé '" + request.getLibelle() + "' existe déjà");
        }

        existing.setLibelle(request.getLibelle());
        existing.setDescription(request.getDescription());

        TypeCommoditer updated = typeCommoditerRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        TypeCommoditer existing = findEntityById(id);
        typeCommoditerRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeCommoditer findEntityById(Long id) {
        return typeCommoditerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + id));
    }

    private TypeCommoditerResponse mapToResponse(TypeCommoditer entity) {
        return TypeCommoditerResponse.builder()
                .id(entity.getId())
                .libelle(entity.getLibelle())
                .description(entity.getDescription())
                .build();
    }
}
