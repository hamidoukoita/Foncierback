package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.AgentPromoteurRequest;
import com.example.foncierback.dto.response.AgentPromoteurResponse;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.TypeFonction;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.repository.TypeFonctionRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.AgentPromoteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentPromoteurServiceImpl implements AgentPromoteurService {

    private final AgentPromoteurRepository agentPromoteurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final TypeFonctionRepository typeFonctionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AgentPromoteurResponse create(AgentPromoteurRequest request) {
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Un utilisateur avec le numéro de téléphone " + request.getTelephone() + " existe déjà");
        }

        SocietePromotrice societe = null;
        if (request.getSocieteId() != null) {
            societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
        }

        TypeFonction typeFonction = null;
        if (request.getTypeFonctionId() != null) {
            typeFonction = typeFonctionRepository.findById(request.getTypeFonctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de fonction introuvable avec l'identifiant : " + request.getTypeFonctionId()));
        }

        AgentPromoteur agent = new AgentPromoteur();
        agent.setNom(request.getNom());
        agent.setPrenom(request.getPrenom());
        agent.setTelephone(request.getTelephone());
        agent.setDateCreation(LocalDateTime.now());
        agent.setStatut(request.getStatut() != null ? request.getStatut() : StatutCompte.ACTIF);

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            agent.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        } else {
            throw new BadRequestException("Le mot de passe est obligatoire pour la création du compte");
        }

        agent.setEstResponsableSociete(request.getEstResponsableSociete() != null && request.getEstResponsableSociete());
        agent.setDateAffectation(request.getDateAffectation());
        agent.setSocietePromotrice(societe);
        agent.setTypeFonction(typeFonction);

        AgentPromoteur saved = agentPromoteurRepository.save(agent);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AgentPromoteurResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentPromoteurResponse> getAll() {
        return agentPromoteurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AgentPromoteurResponse update(Long id, AgentPromoteurRequest request) {
        AgentPromoteur existing = findEntityById(id);

        if (!existing.getTelephone().equals(request.getTelephone())
                && utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Un utilisateur avec le numéro de téléphone " + request.getTelephone() + " existe déjà");
        }

        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setTelephone(request.getTelephone());

        if (request.getStatut() != null) {
            existing.setStatut(request.getStatut());
        }

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            existing.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        if (request.getEstResponsableSociete() != null) {
            existing.setEstResponsableSociete(request.getEstResponsableSociete());
        }

        existing.setDateAffectation(request.getDateAffectation());

        if (request.getSocieteId() != null) {
            SocietePromotrice societe = societePromotriceRepository.findById(request.getSocieteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + request.getSocieteId()));
            existing.setSocietePromotrice(societe);
        } else {
            existing.setSocietePromotrice(null);
        }

        if (request.getTypeFonctionId() != null) {
            TypeFonction typeFonction = typeFonctionRepository.findById(request.getTypeFonctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de fonction introuvable avec l'identifiant : " + request.getTypeFonctionId()));
            existing.setTypeFonction(typeFonction);
        } else {
            existing.setTypeFonction(null);
        }

        AgentPromoteur updated = agentPromoteurRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        AgentPromoteur existing = findEntityById(id);
        agentPromoteurRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentPromoteurResponse> getBySocieteId(Long societeId) {
        if (!societePromotriceRepository.existsById(societeId)) {
            throw new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + societeId);
        }
        return agentPromoteurRepository.findBySocietePromotriceId(societeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AgentPromoteur findEntityById(Long id) {
        return agentPromoteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent promoteur introuvable avec l'identifiant : " + id));
    }

    private AgentPromoteurResponse mapToResponse(AgentPromoteur entity) {
        return AgentPromoteurResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .telephone(entity.getTelephone())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .estResponsableSociete(entity.isEstResponsableSociete())
                .dateAffectation(entity.getDateAffectation())
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .societeNom(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getNom() : null)
                .typeFonctionId(entity.getTypeFonction() != null ? entity.getTypeFonction().getId() : null)
                .typeFonctionLibelle(entity.getTypeFonction() != null ? entity.getTypeFonction().getLibelle() : null)
                .build();
    }
}
