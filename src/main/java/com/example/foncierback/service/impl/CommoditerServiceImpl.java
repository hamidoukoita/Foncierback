package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.CommoditerRequest;
import com.example.foncierback.dto.response.CommoditerResponse;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.entity.Commoditer;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.repository.ProximiteCommoditeRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import org.springframework.security.access.AccessDeniedException;
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
    private final SocietePromotriceRepository societePromotriceRepository;
    private final ProximiteCommoditeRepository proximiteCommoditeRepository;

    @Override
    public CommoditerResponse create(CommoditerRequest request) {
        SocietePromotrice societe = resoudreSociete(request.getSocieteId());
        verifierNomDisponible(request.getNom(), societe);

        TypeCommoditer typeCommodite = null;
        if (request.getTypeCommoditeId() != null) {
            typeCommodite = typeCommoditerRepository.findById(request.getTypeCommoditeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + request.getTypeCommoditeId()));
        }

        Commoditer commoditer = Commoditer.builder()
                .nom(request.getNom())
                .icone(request.getIcone())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .typeCommodite(typeCommodite)
                .societePromotrice(societe)
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
    public List<CommoditerResponse> getBySocieteId(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return commoditerRepository.findBySocietePromotriceId(societeId)
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
        verifierProprietaire(existing);
        if (!existing.getNom().equalsIgnoreCase(request.getNom())) {
            verifierNomDisponible(request.getNom(), existing.getSocietePromotrice());
        }

        TypeCommoditer typeCommodite = null;
        if (request.getTypeCommoditeId() != null) {
            typeCommodite = typeCommoditerRepository.findById(request.getTypeCommoditeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de commodité introuvable avec l'identifiant : " + request.getTypeCommoditeId()));
        }

        existing.setNom(request.getNom());
        existing.setIcone(request.getIcone());
        existing.setDescription(request.getDescription());
        existing.setLatitude(request.getLatitude());
        existing.setLongitude(request.getLongitude());
        existing.setTypeCommodite(typeCommodite);

        Commoditer updated = commoditerRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Commoditer existing = findEntityById(id);
        verifierProprietaire(existing);
        proximiteCommoditeRepository.deleteByCommoditeId(id);
        commoditerRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Commoditer findEntityById(Long id) {
        return commoditerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commodité introuvable avec l'identifiant : " + id));
    }

    /** Un agent agit toujours pour sa propre société ; l'admin peut cibler une société ou le référentiel. */
    private SocietePromotrice resoudreSociete(Long societeIdDemande) {
        Long societeId = SecurityUtils.isAdmin() ? societeIdDemande : SecurityUtils.getCurrentSocieteId();
        if (societeId == null) {
            return null;
        }
        return societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId));
    }

    private void verifierNomDisponible(String nom, SocietePromotrice societe) {
        boolean existe = societe == null
                ? commoditerRepository.existsByNomIgnoreCaseAndSocietePromotriceIsNull(nom)
                : commoditerRepository.existsByNomIgnoreCaseAndSocietePromotriceId(nom, societe.getId());
        if (existe) {
            throw new BadRequestException("Une commodité avec le nom '" + nom + "' existe déjà");
        }
    }

    private void verifierProprietaire(Commoditer commoditer) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        Long societeId = commoditer.getSocietePromotrice() != null ? commoditer.getSocietePromotrice().getId() : null;
        if (societeId == null || !societeId.equals(SecurityUtils.getCurrentSocieteId())) {
            throw new AccessDeniedException("Cette commodité n'appartient pas à votre société");
        }
    }

    @Override
    public CommoditerResponse toResponse(Commoditer commoditer) {
        return mapToResponse(commoditer);
    }

    private CommoditerResponse mapToResponse(Commoditer entity) {
        return CommoditerResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .icone(entity.getIcone())
                .description(entity.getDescription())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .typeCommoditeId(entity.getTypeCommodite() != null ? entity.getTypeCommodite().getId() : null)
                .typeCommoditeLibelle(entity.getTypeCommodite() != null ? entity.getTypeCommodite().getLibelle() : null)
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .build();
    }
}
