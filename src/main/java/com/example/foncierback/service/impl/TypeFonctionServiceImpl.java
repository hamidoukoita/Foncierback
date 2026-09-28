package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.TypeFonctionRequest;
import com.example.foncierback.dto.response.TypeFonctionResponse;
import com.example.foncierback.entity.NiveauAcces;
import com.example.foncierback.entity.TypeFonction;
import com.example.foncierback.repository.NiveauAccesRepository;
import com.example.foncierback.repository.TypeFonctionRepository;
import com.example.foncierback.service.TypeFonctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TypeFonctionServiceImpl implements TypeFonctionService {

    private final TypeFonctionRepository typeFonctionRepository;
    private final NiveauAccesRepository niveauAccesRepository;

    @Override
    public TypeFonctionResponse create(TypeFonctionRequest request) {
        if (typeFonctionRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un type de fonction avec le libellé '" + request.getLibelle() + "' existe déjà");
        }
        if (request.getCode() != null && !request.getCode().isBlank() && typeFonctionRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un type de fonction avec le code '" + request.getCode() + "' existe déjà");
        }

        NiveauAcces niveauAcces = null;
        if (request.getNiveauAccesId() != null) {
            niveauAcces = niveauAccesRepository.findById(request.getNiveauAccesId())
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau d'accès introuvable avec l'identifiant : " + request.getNiveauAccesId()));
        }

        TypeFonction typeFonction = TypeFonction.builder()
                .libelle(request.getLibelle())
                .code(request.getCode())
                .description(request.getDescription())
                .niveauAcces(niveauAcces)
                .build();

        TypeFonction saved = typeFonctionRepository.save(typeFonction);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeFonctionResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeFonctionResponse> getAll() {
        return typeFonctionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeFonctionResponse> getByNiveauAccesId(Long niveauAccesId) {
        if (!niveauAccesRepository.existsById(niveauAccesId)) {
            throw new ResourceNotFoundException("Niveau d'accès introuvable avec l'identifiant : " + niveauAccesId);
        }
        return typeFonctionRepository.findByNiveauAccesId(niveauAccesId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TypeFonctionResponse update(Long id, TypeFonctionRequest request) {
        TypeFonction existing = findEntityById(id);

        if (!existing.getLibelle().equalsIgnoreCase(request.getLibelle())
                && typeFonctionRepository.existsByLibelle(request.getLibelle())) {
            throw new BadRequestException("Un type de fonction avec le libellé '" + request.getLibelle() + "' existe déjà");
        }

        if (request.getCode() != null && !request.getCode().isBlank()
                && !request.getCode().equalsIgnoreCase(existing.getCode())
                && typeFonctionRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un type de fonction avec le code '" + request.getCode() + "' existe déjà");
        }

        NiveauAcces niveauAcces = null;
        if (request.getNiveauAccesId() != null) {
            niveauAcces = niveauAccesRepository.findById(request.getNiveauAccesId())
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau d'accès introuvable avec l'identifiant : " + request.getNiveauAccesId()));
        }

        existing.setLibelle(request.getLibelle());
        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());
        existing.setNiveauAcces(niveauAcces);

        TypeFonction updated = typeFonctionRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        TypeFonction existing = findEntityById(id);
        typeFonctionRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeFonction findEntityById(Long id) {
        return typeFonctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de fonction introuvable avec l'identifiant : " + id));
    }

    private TypeFonctionResponse mapToResponse(TypeFonction entity) {
        return TypeFonctionResponse.builder()
                .id(entity.getId())
                .libelle(entity.getLibelle())
                .code(entity.getCode())
                .description(entity.getDescription())
                .niveauAccesId(entity.getNiveauAcces() != null ? entity.getNiveauAcces().getId() : null)
                .niveauAccesLibelle(entity.getNiveauAcces() != null ? entity.getNiveauAcces().getLibelle() : null)
                .build();
    }
}
