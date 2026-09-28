package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.response.UtilisateurResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.Administrateur;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getAll() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UtilisateurResponse toggleStatut(Long id) {
        Utilisateur utilisateur = findEntityById(id);
        if (utilisateur.getStatut() == StatutCompte.ACTIF) {
            utilisateur.setStatut(StatutCompte.SUSPENDU);
        } else {
            utilisateur.setStatut(StatutCompte.ACTIF);
        }
        Utilisateur updated = utilisateurRepository.save(utilisateur);
        return mapToResponse(updated);
    }

    @Override
    public UtilisateurResponse changeStatut(Long id, StatutCompte statut) {
        Utilisateur utilisateur = findEntityById(id);
        utilisateur.setStatut(statut);
        Utilisateur updated = utilisateurRepository.save(utilisateur);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getByTelephone(String telephone) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec le numéro de téléphone : " + telephone));
        return mapToResponse(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Utilisateur findEntityById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'identifiant : " + id));
    }

    private UtilisateurResponse mapToResponse(Utilisateur entity) {
        return UtilisateurResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .telephone(entity.getTelephone())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .typeUtilisateur(resolveTypeUtilisateur(entity))
                .build();
    }

    private String resolveTypeUtilisateur(Utilisateur utilisateur) {
        Object unproxied = Hibernate.unproxy(utilisateur);
        if (unproxied instanceof Acquereur) {
            return "ACQUEREUR";
        } else if (unproxied instanceof AgentPromoteur) {
            return "AGENT_PROMOTEUR";
        } else if (unproxied instanceof Administrateur) {
            return "ADMINISTRATEUR";
        }
        return "UTILISATEUR";
    }
}
