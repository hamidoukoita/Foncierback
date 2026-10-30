package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.config.security.SecurityUtils;
import com.example.foncierback.dto.request.ChangementMotDePasseRequest;
import com.example.foncierback.dto.request.ChangementTelephoneRequest;
import com.example.foncierback.dto.request.ReinitialisationMotDePasseRequest;
import com.example.foncierback.dto.response.AuthResponse;
import com.example.foncierback.dto.response.CodeSimuleResponse;
import com.example.foncierback.entity.Acquereur;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.AuthService;
import com.example.foncierback.service.CodesVerification;
import com.example.foncierback.service.CompteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CompteServiceImpl implements CompteService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final CodesVerification codes;

    @Override
    public AuthResponse changerTelephone(ChangementTelephoneRequest request) {
        Utilisateur user = utilisateurConnecte();
        verifierMotDePasse(user, request.getMotDePasse());
        // Seuls les acquéreurs (diaspora comprise) peuvent avoir un numéro étranger.
        if (!(user instanceof com.example.foncierback.entity.Acquereur)
                && !com.example.foncierback.common.validation.Telephones.estMalien(request.getNouveauTelephone())) {
            throw new BadRequestException(com.example.foncierback.common.validation.Telephones.messageInvalide(
                    request.getNouveauTelephone(), true));
        }
        if (Objects.equals(user.getTelephone(), request.getNouveauTelephone())) {
            throw new BadRequestException("C'est déjà votre numéro actuel.");
        }
        if (utilisateurRepository.existsByTelephone(request.getNouveauTelephone())) {
            throw new BadRequestException("Ce numéro est déjà utilisé par un autre compte.");
        }
        user.setTelephone(request.getNouveauTelephone());
        return authService.sessionPour(utilisateurRepository.save(user));
    }

    @Override
    public void changerMotDePasse(ChangementMotDePasseRequest request) {
        Utilisateur user = utilisateurConnecte();
        verifierMotDePasse(user, request.getAncienMotDePasse());
        if (passwordEncoder.matches(request.getNouveauMotDePasse(), user.getMotDePasse())) {
            throw new BadRequestException("Le nouveau mot de passe doit être différent de l'actuel.");
        }
        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public CodeSimuleResponse demanderCodeReinitialisation(String telephone) {
        acquereurParTelephone(telephone);
        return new CodeSimuleResponse(telephone, codes.generer(telephone), CodesVerification.VALIDITE_MINUTES);
    }

    @Override
    public void reinitialiserMotDePasse(ReinitialisationMotDePasseRequest request) {
        Utilisateur user = acquereurParTelephone(request.getTelephone());
        if (!codes.verifier(request.getTelephone(), request.getCode())) {
            throw new BadRequestException("Code incorrect ou expiré. Demandez un nouveau code.");
        }
        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
    }

    // ------------------------------------------------------------------ Outils

    private Utilisateur utilisateurConnecte() {
        Long id = SecurityUtils.getCurrentUserId();
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private void verifierMotDePasse(Utilisateur user, String motDePasse) {
        if (motDePasse == null || !passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new BadRequestException("Mot de passe actuel incorrect.");
        }
    }

    /** La récupération par code ne concerne que les comptes acquéreurs (application mobile). */
    private Utilisateur acquereurParTelephone(String telephone) {
        return utilisateurRepository.findByTelephone(telephone)
                .filter(Acquereur.class::isInstance)
                .orElseThrow(() -> new BadRequestException("Aucun compte acquéreur n'est associé à ce numéro."));
    }
}
