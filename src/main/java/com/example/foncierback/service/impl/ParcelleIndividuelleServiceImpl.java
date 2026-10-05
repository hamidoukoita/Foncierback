package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ParcelleIndividuelleRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.dto.response.ParcelleIndividuelleResponse;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.ParcelleIndividuelleRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ParcelleIndividuelleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParcelleIndividuelleServiceImpl implements ParcelleIndividuelleService {

    private final ParcelleIndividuelleRepository parcelleIndividuelleRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final CommoditerRepository commoditerRepository;

    @Override
    public ParcelleIndividuelleResponse create(ParcelleIndividuelleRequest request) {
        if (parcelleIndividuelleRepository.existsByNumeroTitreFoncier(request.getNumeroTitreFoncier())) {
            throw new BadRequestException("Une parcelle individuelle avec le Titre Foncier '" + request.getNumeroTitreFoncier() + "' existe déjà");
        }

        String reference = request.getReference();
        if (reference == null || reference.trim().isEmpty()) {
            reference = "PARC-TF" + request.getNumeroTitreFoncier().replaceAll("\\s+", "");
        }

        if (bienFoncierRepository.existsByReference(reference)) {
            throw new BadRequestException("Un bien foncier avec la référence '" + reference + "' existe déjà");
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
            if (societe.getStatutAgrement() != com.example.foncierback.entity.enums.StatutAgrement.VERIFIER) {
                throw new IllegalStateException("La société n'a pas encore validé son dossier KYC. Création de parcelle bloquée.");
            }
        }

        List<Commoditer> commodites = new ArrayList<>();
        if (request.getCommoditeIds() != null && !request.getCommoditeIds().isEmpty()) {
            commodites = commoditerRepository.findAllById(request.getCommoditeIds());
        }

        ParcelleIndividuelle parcelle = ParcelleIndividuelle.builder()
                .reference(reference)
                .superficie(request.getSuperficie())
                .prix(request.getPrix())
                .facade(request.getFacade())
                .profondeur(request.getProfondeur())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .geometryJson(request.getGeometryJson())
                .statut(request.getStatut())
                .numeroTitreFoncier(request.getNumeroTitreFoncier())
                .murCloture(Boolean.TRUE.equals(request.getMurCloture()))
                .eauSomapep(Boolean.TRUE.equals(request.getEauSomapep()))
                .electriciteEdm(Boolean.TRUE.equals(request.getElectriciteEdm()))
                .voieBitumee(Boolean.TRUE.equals(request.getVoieBitumee()))
                .societePromotrice(societe)
                .commodites(commodites)
                .build();

        ParcelleIndividuelle saved = parcelleIndividuelleRepository.save(parcelle);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelleIndividuelleResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelleIndividuelleResponse> getAll() {
        return parcelleIndividuelleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ParcelleIndividuelleResponse update(Long id, ParcelleIndividuelleRequest request) {
        ParcelleIndividuelle existing = findEntityById(id);

        if (!existing.getNumeroTitreFoncier().equalsIgnoreCase(request.getNumeroTitreFoncier())
                && parcelleIndividuelleRepository.existsByNumeroTitreFoncierAndIdNot(request.getNumeroTitreFoncier(), id)) {
            throw new BadRequestException("Une parcelle individuelle avec le Titre Foncier '" + request.getNumeroTitreFoncier() + "' existe déjà");
        }

        if (request.getReference() != null && !request.getReference().trim().isEmpty()) {
            if (!existing.getReference().equalsIgnoreCase(request.getReference())
                    && bienFoncierRepository.existsByReferenceAndIdNot(request.getReference(), id)) {
                throw new BadRequestException("Un bien foncier avec la référence '" + request.getReference() + "' existe déjà");
            }
            existing.setReference(request.getReference());
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
        }

        if (request.getCommoditeIds() != null) {
            List<Commoditer> commodites = commoditerRepository.findAllById(request.getCommoditeIds());
            existing.setCommodites(commodites);
        }

        existing.setNumeroTitreFoncier(request.getNumeroTitreFoncier());
        existing.setSuperficie(request.getSuperficie());
        existing.setPrix(request.getPrix());
        existing.setFacade(request.getFacade());
        existing.setProfondeur(request.getProfondeur());
        existing.setLatitude(request.getLatitude());
        existing.setLongitude(request.getLongitude());
        existing.setGeometryJson(request.getGeometryJson());
        existing.setStatut(request.getStatut());
        existing.setMurCloture(Boolean.TRUE.equals(request.getMurCloture()));
        existing.setEauSomapep(Boolean.TRUE.equals(request.getEauSomapep()));
        existing.setElectriciteEdm(Boolean.TRUE.equals(request.getElectriciteEdm()));
        existing.setVoieBitumee(Boolean.TRUE.equals(request.getVoieBitumee()));
        existing.setSocietePromotrice(societe);

        ParcelleIndividuelle updated = parcelleIndividuelleRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        ParcelleIndividuelle existing = findEntityById(id);
        parcelleIndividuelleRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelleIndividuelleResponse> getBySocieteId(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return parcelleIndividuelleRepository.findBySocietePromotriceId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelleIndividuelle findEntityById(Long id) {
        return parcelleIndividuelleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle individuelle introuvable avec l'identifiant : " + id));
    }

    private ParcelleIndividuelleResponse mapToResponse(ParcelleIndividuelle entity) {
        List<CommoditerResponse> commodites = entity.getCommodites() != null
                ? entity.getCommodites().stream().map(this::mapCommoditerToResponse).toList()
                : new ArrayList<>();

        return ParcelleIndividuelleResponse.builder()
                .id(entity.getId())
                .reference(entity.getReference())
                .numeroTitreFoncier(entity.getNumeroTitreFoncier())
                .superficie(entity.getSuperficie())
                .prix(entity.getPrix())
                .facade(entity.getFacade())
                .profondeur(entity.getProfondeur())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .geometryJson(entity.getGeometryJson())
                .statut(entity.getStatut())
                .murCloture(entity.getMurCloture())
                .eauSomapep(entity.getEauSomapep())
                .electriciteEdm(entity.getElectriciteEdm())
                .voieBitumee(entity.getVoieBitumee())
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .societeNom(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getNom() : null)
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
