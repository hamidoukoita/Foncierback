package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.NiveauAccesRequest;
import com.example.foncierback.dto.response.NiveauAccesResponse;
import com.example.foncierback.entity.NiveauAcces;
import com.example.foncierback.repository.NiveauAccesRepository;
import com.example.foncierback.service.NiveauAccesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NiveauAccesServiceImpl implements NiveauAccesService {

    private final NiveauAccesRepository niveauAccesRepository;

    @Override
    public NiveauAccesResponse create(NiveauAccesRequest request) {
        if (niveauAccesRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un niveau d'accès avec le libellé '" + request.getLibelle() + "' existe déjà");
        }
        if (request.getCode() != null && !request.getCode().isBlank() && niveauAccesRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un niveau d'accès avec le code '" + request.getCode() + "' existe déjà");
        }

        NiveauAcces niveauAcces = NiveauAcces.builder()
                .libelle(request.getLibelle())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        NiveauAcces saved = niveauAccesRepository.save(niveauAcces);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NiveauAccesResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NiveauAccesResponse> getAll() {
        return niveauAccesRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public NiveauAccesResponse update(Long id, NiveauAccesRequest request) {
        NiveauAcces existing = findEntityById(id);

        if (!existing.getLibelle().equalsIgnoreCase(request.getLibelle())
                && niveauAccesRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un niveau d'accès avec le libellé '" + request.getLibelle() + "' existe déjà");
        }

        if (request.getCode() != null && !request.getCode().isBlank()
                && !request.getCode().equalsIgnoreCase(existing.getCode())
                && niveauAccesRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un niveau d'accès avec le code '" + request.getCode() + "' existe déjà");
        }

        existing.setLibelle(request.getLibelle());
        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());

        NiveauAcces updated = niveauAccesRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        NiveauAcces existing = findEntityById(id);
        niveauAccesRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public NiveauAcces findEntityById(Long id) {
        return niveauAccesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau d'accès introuvable avec l'identifiant : " + id));
    }

    private NiveauAccesResponse mapToResponse(NiveauAcces entity) {
        return NiveauAccesResponse.builder()
                .id(entity.getId())
                .libelle(entity.getLibelle())
                .code(entity.getCode())
                .description(entity.getDescription())
                .build();
    }
}
