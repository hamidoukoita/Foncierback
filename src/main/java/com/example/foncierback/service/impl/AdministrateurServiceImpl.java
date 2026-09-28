package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.AdministrateurRequest;
import com.example.foncierback.dto.response.AdministrateurResponse;
import com.example.foncierback.entity.Administrateur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.AdministrateurRepository;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.AdministrateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministrateurServiceImpl implements AdministrateurService {

    private final AdministrateurRepository administrateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdministrateurResponse create(AdministrateurRequest request) {
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Un utilisateur avec le numéro de téléphone " + request.getTelephone() + " existe déjà");
        }

        Administrateur administrateur = new Administrateur();
        administrateur.setNom(request.getNom());
        administrateur.setPrenom(request.getPrenom());
        administrateur.setTelephone(request.getTelephone());
        administrateur.setDateCreation(LocalDateTime.now());
        administrateur.setStatut(request.getStatut() != null ? request.getStatut() : StatutCompte.ACTIF);

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            administrateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        } else {
            throw new BadRequestException("Le mot de passe est obligatoire pour la création du compte");
        }

        Administrateur saved = administrateurRepository.save(administrateur);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdministrateurResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministrateurResponse> getAll() {
        return administrateurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdministrateurResponse update(Long id, AdministrateurRequest request) {
        Administrateur existing = findEntityById(id);

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

        Administrateur updated = administrateurRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Administrateur existing = findEntityById(id);
        administrateurRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Administrateur findEntityById(Long id) {
        return administrateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur introuvable avec l'identifiant : " + id));
    }

    private AdministrateurResponse mapToResponse(Administrateur entity) {
        return AdministrateurResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .telephone(entity.getTelephone())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .build();
    }
}
