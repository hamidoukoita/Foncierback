package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.ProximiteCommoditeRequest;
import com.example.foncierback.dto.response.ProximiteCommoditeResponse;
import com.example.foncierback.entity.*;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.CommoditerRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.repository.ProximiteCommoditeRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.CommoditerService;
import com.example.foncierback.service.ProximiteCommoditeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProximiteCommoditeServiceImpl implements ProximiteCommoditeService {

    private final ProximiteCommoditeRepository proximiteRepository;
    private final CommoditerRepository commoditerRepository;
    private final ProgrammeFoncierRepository programmeFoncierRepository;
    private final BienFoncierRepository bienFoncierRepository;
    private final CommoditerService commoditerService;
    private final SocietePromotriceRepository societePromotriceRepository;

    @Override
    public ProximiteCommoditeResponse create(ProximiteCommoditeRequest request) {
        verifierCibleUnique(request);
        Commoditer commodite = commoditerRepository.findById(request.getCommoditeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodité introuvable avec l'identifiant : " + request.getCommoditeId()));

        ProximiteCommodite proximite = ProximiteCommodite.builder()
                .commodite(commodite)
                .distanceKm(request.getDistanceKm())
                .build();
        appliquerCible(proximite, request);
        verifierSociete(proximite);
        verifierDoublon(request);

        return mapToResponse(proximiteRepository.save(proximite));
    }

    @Override
    public ProximiteCommoditeResponse update(Long id, ProximiteCommoditeRequest request) {
        ProximiteCommodite existing = findEntityById(id);
        verifierSociete(existing);
        // La commodité et la cible restent fixes : seule la distance est modifiable.
        existing.setDistanceKm(request.getDistanceKm());
        return mapToResponse(proximiteRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        ProximiteCommodite existing = findEntityById(id);
        verifierSociete(existing);
        proximiteRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ProximiteCommoditeResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProximiteCommoditeResponse> findByProgramme(Long programmeId) {
        return proximiteRepository.findByProgrammeFoncierId(programmeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProximiteCommoditeResponse> findByBien(Long bienId) {
        return proximiteRepository.findByBienFoncierId(bienId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProximiteCommoditeResponse> findBySociete(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société introuvable : " + societeId);
        }
        return proximiteRepository.findBySocieteId(societeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProximiteCommodite findEntityById(Long id) {
        return proximiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rattachement introuvable avec l'identifiant : " + id));
    }

    private void verifierCibleUnique(ProximiteCommoditeRequest request) {
        boolean programme = request.getProgrammeId() != null;
        boolean bien = request.getBienId() != null;
        if (programme == bien) {
            throw new BadRequestException("Indiquez soit une cité, soit une parcelle (et une seule)");
        }
    }

    private void appliquerCible(ProximiteCommodite proximite, ProximiteCommoditeRequest request) {
        if (request.getProgrammeId() != null) {
            proximite.setProgrammeFoncier(programmeFoncierRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programme introuvable avec l'identifiant : " + request.getProgrammeId())));
        } else {
            proximite.setBienFoncier(bienFoncierRepository.findById(request.getBienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bien introuvable avec l'identifiant : " + request.getBienId())));
        }
    }

    private void verifierDoublon(ProximiteCommoditeRequest request) {
        boolean existe = request.getProgrammeId() != null
                ? proximiteRepository.existsByCommoditeIdAndProgrammeFoncierId(request.getCommoditeId(), request.getProgrammeId())
                : proximiteRepository.existsByCommoditeIdAndBienFoncierId(request.getCommoditeId(), request.getBienId());
        if (existe) {
            throw new BadRequestException("Cette commodité est déjà rattachée à ce bien");
        }
    }

    /** Un agent ne rattache que les commodités de sa société à ses propres biens. */
    private void verifierSociete(ProximiteCommodite proximite) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        Long societeId = SecurityUtils.getCurrentSocieteId();
        Long societeCommodite = idSociete(proximite.getCommodite().getSocietePromotrice());
        Long societeCible = proximite.getProgrammeFoncier() != null
                ? idSociete(proximite.getProgrammeFoncier().getSocietePromotrice())
                : societeDuBien(proximite.getBienFoncier());
        boolean commoditeAutorisee = societeCommodite == null || societeCommodite.equals(societeId);
        if (societeId == null || !commoditeAutorisee || !societeId.equals(societeCible)) {
            throw new AccessDeniedException("Ce rattachement ne concerne pas votre société");
        }
    }

    private Long societeDuBien(BienFoncier bien) {
        return bien != null ? idSociete(bien.societeGestionnaire()) : null;
    }

    private Long idSociete(SocietePromotrice societe) {
        return societe != null ? societe.getId() : null;
    }

    private ProximiteCommoditeResponse mapToResponse(ProximiteCommodite entity) {
        ProgrammeFoncier programme = entity.getProgrammeFoncier();
        BienFoncier bien = entity.getBienFoncier();
        return ProximiteCommoditeResponse.builder()
                .id(entity.getId())
                .distanceKm(entity.getDistanceKm())
                .programmeId(programme != null ? programme.getId() : null)
                .programmeNom(programme != null ? programme.getNom() : null)
                .bienId(bien != null ? bien.getId() : null)
                .bienReference(bien != null ? bien.getReference() : null)
                .commodite(commoditerService.toResponse(entity.getCommodite()))
                .build();
    }
}
