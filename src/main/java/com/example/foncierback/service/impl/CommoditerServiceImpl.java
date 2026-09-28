package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.CommoditerRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.TypeCommoditer;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.TypeCommoditerRepository;
import com.example.foncierback.service.CommoditerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommoditerServiceImpl implements CommoditerService {

    private final CommoditerRepository commoditerRepository;
    private final TypeCommoditerRepository typeCommoditerRepository;

    @Override
    public CommoditerResponse create(CommoditerRequest request) {
        if (commoditerRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une commodité avec le nom '" + request.getNom() + "' existe déjà");
        }

        TypeCommoditer typeCommodite = null;
        if (request.getTypeCommoditeId() != null) {
            typeCommodite = typeCommoditerRepository.findById(request.getTypeCommoditeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + request.getTypeCommoditeId()));
        }

        Commoditer commoditer = Commoditer.builder()
                .nom(request.getNom())
                .icone(request.getIcone())
                .description(request.getDescription())
                .typeCommodite(typeCommodite)
                .build();

        Commoditer saved = commoditerRepository.save(commoditer);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommoditerResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommoditerResponse> getAll() {
        return commoditerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommoditerResponse> getByTypeCommoditeId(Long typeCommoditeId) {
        if (!typeCommoditerRepository.existsById(typeCommoditeId)) {
            throw new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + typeCommoditeId);
        }
        return commoditerRepository.findByTypeCommoditeId(typeCommoditeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommoditerResponse> searchByNom(String keyword) {
        return commoditerRepository.findByNomContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CommoditerResponse update(Long id, CommoditerRequest request) {
        Commoditer existing = findEntityById(id);

        if (!existing.getNom().equalsIgnoreCase(request.getNom())
                && commoditerRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une commodité avec le nom '" + request.getNom() + "' existe déjà");
        }

        TypeCommoditer typeCommodite = null;
        if (request.getTypeCommoditeId() != null) {
            typeCommodite = typeCommoditerRepository.findById(request.getTypeCommoditeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + request.getTypeCommoditeId()));
        }

        existing.setNom(request.getNom());
        existing.setIcone(request.getIcone());
        existing.setDescription(request.getDescription());
        existing.setTypeCommodite(typeCommodite);

        Commoditer updated = commoditerRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Commoditer existing = findEntityById(id);
        commoditerRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Commoditer findEntityById(Long id) {
        return commoditerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commodité introuvable avec l'identifiant : " + id));
    }

    private CommoditerResponse mapToResponse(Commoditer entity) {
        return CommoditerResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .icone(entity.getIcone())
                .description(entity.getDescription())
                .typeCommoditeId(entity.getTypeCommodite() != null ? entity.getTypeCommodite().getId() : null)
                .typeCommoditeLibelle(entity.getTypeCommodite() != null ? entity.getTypeCommodite().getLibelle() : null)
                .build();
    }
}
