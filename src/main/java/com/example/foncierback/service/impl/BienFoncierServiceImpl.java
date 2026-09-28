package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.response.BienFoncierResponse;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.LotProgramme;
import com.example.foncierback.entity.ParcelleIndividuelle;
import com.example.foncierback.entity.enums.StatutParcelle;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.service.BienFoncierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BienFoncierServiceImpl implements BienFoncierService {

    private final BienFoncierRepository bienFoncierRepository;

    @Override
    @Transactional(readOnly = true)
    public BienFoncierResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienFoncierResponse> getAll() {
        return bienFoncierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienFoncierResponse> getByStatut(StatutParcelle statut) {
        return bienFoncierRepository.findByStatut(statut)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BienFoncier findEntityById(Long id) {
        return bienFoncierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'identifiant : " + id));
    }

    private BienFoncierResponse mapToResponse(BienFoncier entity) {
        List<CommoditerResponse> commodites = entity.getCommodites() != null
                ? entity.getCommodites().stream().map(this::mapCommoditerToResponse).toList()
                : new ArrayList<>();

        BienFoncierResponse.BienFoncierResponseBuilder builder = BienFoncierResponse.builder()
                .id(entity.getId())
                .reference(entity.getReference())
                .superficie(entity.getSuperficie())
                .prix(entity.getPrix())
                .facade(entity.getFacade())
                .profondeur(entity.getProfondeur())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .statut(entity.getStatut())
                .commodites(commodites);

        if (entity instanceof LotProgramme lot) {
            builder.typeBien("LOT_PROGRAMME")
                    .numeroLot(lot.getNumeroLot())
                    .numeroIlot(lot.getNumeroIlot())
                    .numeroIlotLotissement(lot.getNumeroIlotLotissement())
                    .programmeId(lot.getProgrammeFoncier() != null ? lot.getProgrammeFoncier().getId() : null)
                    .programmeNom(lot.getProgrammeFoncier() != null ? lot.getProgrammeFoncier().getNom() : null);
        } else if (entity instanceof ParcelleIndividuelle parcelle) {
            builder.typeBien("PARCELLE_INDIVIDUELLE")
                    .numeroTitreFoncier(parcelle.getNumeroTitreFoncier())
                    .murCloture(parcelle.getMurCloture())
                    .eauSomapep(parcelle.getEauSomapep())
                    .electriciteEdm(parcelle.getElectriciteEdm())
                    .voieBitumee(parcelle.getVoieBitumee())
                    .societeId(parcelle.getSocietePromotrice() != null ? parcelle.getSocietePromotrice().getId() : null)
                    .societeNom(parcelle.getSocietePromotrice() != null ? parcelle.getSocietePromotrice().getNom() : null);
        } else {
            builder.typeBien("BIEN_FONCIER");
        }

        return builder.build();
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
