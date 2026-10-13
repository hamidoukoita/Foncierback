package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ProximiteCommoditeRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.dto.response.ProximiteCommoditeResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.entity.ProximiteCommodite;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.repository.ProximiteCommoditeRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ProximiteCommoditeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProximiteCommoditeServiceImpl implements ProximiteCommoditeService {

    private final ProximiteCommoditeRepository proximiteCommoditeRepository;
    private final CommoditerRepository commoditerRepository;
    private final ProgrammeFoncierRepository programmeFoncierRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final SocietePromotriceRepository societePromotriceRepository;

    @Override
    public ProximiteCommoditeResponse create(ProximiteCommoditeRequest request) {
        validateLinks(request);
        Commoditer commodite = commoditerRepository.findById(request.getCommoditeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodité introuvable : " + request.getCommoditeId()));

        ProgrammeFoncier programme = null;
        if (request.getProgrammeId() != null) {
            programme = programmeFoncierRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programme introuvable : " + request.getProgrammeId()));
        }

        BienFoncier bien = null;
        if (request.getBienId() != null) {
            bien = bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien introuvable : " + request.getBienId()));
        }

        ProximiteCommodite entity = ProximiteCommodite.builder()
                .commodite(commodite)
                .programmeFoncier(programme)
                .bienFoncier(bien)
                .distanceKm(request.getDistanceKm())
                .build();

        return mapToResponse(proximiteCommoditeRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProximiteCommoditeResponse getById(Long id) {
        return mapToResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProximiteCommoditeResponse> getBySocieteId(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société introuvable : " + societeId);
        }
        return proximiteCommoditeRepository.findBySocieteId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProximiteCommoditeResponse update(Long id, ProximiteCommoditeRequest request) {
        validateLinks(request);
        ProximiteCommodite existing = findEntity(id);

        Commoditer commodite = commoditerRepository.findById(request.getCommoditeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodité introuvable : " + request.getCommoditeId()));
        existing.setCommodite(commodite);
        existing.setDistanceKm(request.getDistanceKm());

        if (request.getProgrammeId() != null) {
            existing.setProgrammeFoncier(programmeFoncierRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programme introuvable : " + request.getProgrammeId())));
        } else {
            existing.setProgrammeFoncier(null);
        }

        if (request.getBienId() != null) {
            existing.setBienFoncier(bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien introuvable : " + request.getBienId())));
        } else {
            existing.setBienFoncier(null);
        }

        return mapToResponse(proximiteCommoditeRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        proximiteCommoditeRepository.delete(findEntity(id));
    }

    private void validateLinks(ProximiteCommoditeRequest request) {
        if (request.getProgrammeId() == null && request.getBienId() == null) {
            throw new BadRequestException("Indiquez un programme ou une parcelle pour le rattachement.");
        }
    }

    private ProximiteCommodite findEntity(Long id) {
        return proximiteCommoditeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rattachement commodité introuvable : " + id));
    }

    private ProximiteCommoditeResponse mapToResponse(ProximiteCommodite entity) {
        Commoditer c = entity.getCommodite();
        CommoditerResponse commoditeResponse = null;
        if (c != null) {
            commoditeResponse = CommoditerResponse.builder()
                    .id(c.getId())
                    .nom(c.getNom())
                    .icone(c.getIcone())
                    .description(c.getDescription())
                    .typeCommoditeId(c.getTypeCommodite() != null ? c.getTypeCommodite().getId() : null)
                    .typeCommoditeLibelle(c.getTypeCommodite() != null ? c.getTypeCommodite().getLibelle() : null)
                    .societeId(c.getSocietePromotrice() != null ? c.getSocietePromotrice().getId() : null)
                    .latitude(c.getLatitude())
                    .longitude(c.getLongitude())
                    .build();
        }

        ProgrammeFoncier prog = entity.getProgrammeFoncier();
        BienFoncier bien = entity.getBienFoncier();

        return ProximiteCommoditeResponse.builder()
                .id(entity.getId())
                .distanceKm(entity.getDistanceKm())
                .programmeId(prog != null ? prog.getId() : null)
                .programmeNom(prog != null ? prog.getNom() : null)
                .bienId(bien != null ? bien.getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .commodite(commoditeResponse)
                .build();
    }
}
