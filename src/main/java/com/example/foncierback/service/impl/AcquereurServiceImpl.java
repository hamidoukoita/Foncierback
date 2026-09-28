package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.response.AcquereurResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.AcquereurRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.AcquereurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AcquereurServiceImpl implements AcquereurService {

    private final AcquereurRepository acquereurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AcquereurResponse create(AcquereurRequest request) {
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Un utilisateur avec le numéro de téléphone " + request.getTelephone() + " existe déjà");
        }

        Acquereur acquereur = new Acquereur();
        acquereur.setNom(request.getNom());
        acquereur.setPrenom(request.getPrenom());
        acquereur.setTelephone(request.getTelephone());
        acquereur.setDateCreation(LocalDateTime.now());
        acquereur.setStatut(request.getStatut() != null ? request.getStatut() : StatutCompte.ACTIF);

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            acquereur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        } else {
            throw new BadRequestException("Le mot de passe est obligatoire pour la création du compte");
        }

        acquereur.setPaysResidence(request.getPaysResidence());
        acquereur.setPreference(request.getPreference());

        Acquereur saved = acquereurRepository.save(acquereur);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AcquereurResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcquereurResponse> getAll() {
        return acquereurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AcquereurResponse update(Long id, AcquereurRequest request) {
        Acquereur existing = findEntityById(id);

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

        existing.setPaysResidence(request.getPaysResidence());
        existing.setPreference(request.getPreference());

        Acquereur updated = acquereurRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Acquereur existing = findEntityById(id);
        acquereurRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Acquereur findEntityById(Long id) {
        return acquereurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquéreur introuvable avec l'identifiant : " + id));
    }

    private AcquereurResponse mapToResponse(Acquereur entity) {
        return AcquereurResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .telephone(entity.getTelephone())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .paysResidence(entity.getPaysResidence())
                .preference(entity.getPreference())
                .build();
    }
}
