package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.LotProgrammeRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.dto.response.LotProgrammeResponse;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.LotProgrammeRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.service.LotProgrammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LotProgrammeServiceImpl implements LotProgrammeService {

    private final LotProgrammeRepository lotProgrammeRepository;
    private final ProgrammeFoncierRepository programmeFoncierRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final CommoditerRepository commoditerRepository;

    @Override
    public LotProgrammeResponse create(LotProgrammeRequest request) {
        ProgrammeFoncier programme = programmeFoncierRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + request.getProgrammeId()));

        if (lotProgrammeRepository.existsByProgrammeFoncierIdAndNumeroLot(programme.getId(), request.getNumeroLot())) {
            throw new BadRequestException("Un lot avec le numéro '" + request.getNumeroLot() + "' existe déjà dans ce programme");
        }

        String reference = request.getReference();
        if (reference == null || reference.trim().isEmpty()) {
            reference = "LOT-PRG" + programme.getId() + "-IL" + request.getNumeroIlot() + "-LT" + request.getNumeroLot();
        }

        if (bienFoncierRepository.existsByReference(reference)) {
            throw new BadRequestException("Un bien foncier avec la référence '" + reference + "' existe déjà");
        }

        List<Commoditer> commodites = new ArrayList<>();
        if (request.getCommoditeIds() != null && !request.getCommoditeIds().isEmpty()) {
            commodites = commoditerRepository.findAllById(request.getCommoditeIds());
        }

        LotProgramme lot = LotProgramme.builder()
                .reference(reference)
                .superficie(request.getSuperficie())
                .prix(request.getPrix())
                .facade(request.getFacade())
                .profondeur(request.getProfondeur())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .geometryJson(request.getGeometryJson())
                .statut(request.getStatut())
                .numeroLot(request.getNumeroLot())
                .numeroIlot(request.getNumeroIlot())
                .numeroIlotLotissement(request.getNumeroIlotLotissement())
                .programmeFoncier(programme)
                .commodites(commodites)
                .build();

        LotProgramme saved = lotProgrammeRepository.save(lot);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LotProgrammeResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotProgrammeResponse> getAll() {
        return lotProgrammeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LotProgrammeResponse update(Long id, LotProgrammeRequest request) {
        LotProgramme existing = findEntityById(id);

        ProgrammeFoncier programme = existing.getProgrammeFoncier();
        if (request.getProgrammeId() != null && !request.getProgrammeId().equals(programme.getId())) {
            programme = programmeFoncierRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + request.getProgrammeId()));
        }

        if (lotProgrammeRepository.existsByProgrammeFoncierIdAndNumeroLotAndIdNot(programme.getId(), request.getNumeroLot(), id)) {
            throw new BadRequestException("Un lot avec le numéro '" + request.getNumeroLot() + "' existe déjà dans ce programme");
        }

        if (request.getReference() != null && !request.getReference().trim().isEmpty()) {
            if (!existing.getReference().equalsIgnoreCase(request.getReference())
                    && bienFoncierRepository.existsByReferenceAndIdNot(request.getReference(), id)) {
                throw new BadRequestException("Un bien foncier avec la référence '" + request.getReference() + "' existe déjà");
            }
            existing.setReference(request.getReference());
        }

        if (request.getCommoditeIds() != null) {
            List<Commoditer> commodites = commoditerRepository.findAllById(request.getCommoditeIds());
            existing.setCommodites(commodites);
        }

        existing.setProgrammeFoncier(programme);
        existing.setNumeroLot(request.getNumeroLot());
        existing.setNumeroIlot(request.getNumeroIlot());
        existing.setNumeroIlotLotissement(request.getNumeroIlotLotissement());
        existing.setSuperficie(request.getSuperficie());
        existing.setPrix(request.getPrix());
        existing.setFacade(request.getFacade());
        existing.setProfondeur(request.getProfondeur());
        existing.setLatitude(request.getLatitude());
        existing.setLongitude(request.getLongitude());
        // Ne pas effacer la forme du lot quand la requête ne transporte pas de géométrie.
        if (request.getGeometryJson() != null) {
            existing.setGeometryJson(request.getGeometryJson());
        }
        existing.setStatut(request.getStatut());

        LotProgramme updated = lotProgrammeRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public LotProgrammeResponse updateGeometry(Long id, String geometryJson) {
        LotProgramme existing = findEntityById(id);
        existing.setGeometryJson(geometryJson);
        LotProgramme updated = lotProgrammeRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        LotProgramme existing = findEntityById(id);
        lotProgrammeRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotProgrammeResponse> getByProgrammeId(Long programmeId) {
        if (!programmeFoncierRepository.existsById(programmeId)) {
            throw new ResourceNotFoundException("Programme foncier introuvable avec l'identifiant : " + programmeId);
        }
        return lotProgrammeRepository.findByProgrammeFoncierId(programmeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LotProgramme findEntityById(Long id) {
        return lotProgrammeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot de programme introuvable avec l'identifiant : " + id));
    }

    private LotProgrammeResponse mapToResponse(LotProgramme entity) {
        List<CommoditerResponse> commodites = entity.getCommodites() != null
                ? entity.getCommodites().stream().map(this::mapCommoditerToResponse).toList()
                : new ArrayList<>();

        return LotProgrammeResponse.builder()
                .id(entity.getId())
                .reference(entity.getReference())
                .superficie(entity.getSuperficie())
                .prix(entity.getPrix())
                .facade(entity.getFacade())
                .profondeur(entity.getProfondeur())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .geometryJson(entity.getGeometryJson())
                .statut(entity.getStatut())
                .numeroLot(entity.getNumeroLot())
                .numeroIlot(entity.getNumeroIlot())
                .numeroIlotLotissement(entity.getNumeroIlotLotissement())
                .programmeId(entity.getProgrammeFoncier() != null ? entity.getProgrammeFoncier().getId() : null)
                .programmeNom(entity.getProgrammeFoncier() != null ? entity.getProgrammeFoncier().getNom() : null)
                .commodites(commodites)
                .build();
    }

    private CommoditerResponse mapCommoditerToResponse(Commoditer c) {
        if (c == null) return null;
        return CommoditerResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .icone(c.getIcone())
                .description(c.getDescription())
                .typeCommoditeId(c.getTypeCommodite() != null ? c.getTypeCommodite().getId() : null)
                .typeCommoditeLibelle(c.getTypeCommodite() != null ? c.getTypeCommodite().getLibelle() : null)
                .build();
    }
}
