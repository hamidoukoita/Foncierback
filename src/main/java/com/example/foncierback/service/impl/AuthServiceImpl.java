package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.config.security.CustomUserDetails;
import com.example.foncierback.config.security.JwtService;
import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.request.LoginRequest;
import com.example.foncierback.dto.response.AcquereurResponse;
import com.example.foncierback.dto.response.AuthResponse;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.repository.UtilisateurRepository;
import com.example.foncierback.service.AcquereurService;
import com.example.foncierback.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AcquereurService acquereurService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        Utilisateur user = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new BadRequestException("Téléphone ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new BadRequestException("Téléphone ou mot de passe incorrect");
        }

        if (user.getStatut() == StatutCompte.SUSPENDU || user.getStatut() == StatutCompte.DESACTIVE) {
            throw new BadRequestException("Compte suspendu ou désactivé");
        }

        if (user.getStatut() != StatutCompte.ACTIF) {
            throw new BadRequestException("Compte non actif : " + user.getStatut());
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .telephone(user.getTelephone())
                .role(userDetails.getRole())
                .statut(user.getStatut())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerAcquereur(AcquereurRequest request) {
        if (request.getStatut() == null) {
            request.setStatut(StatutCompte.ACTIF);
        }

        AcquereurResponse created = acquereurService.create(request);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setTelephone(request.getTelephone());
        loginRequest.setMotDePasse(request.getMotDePasse());

        return login(loginRequest);
    }
}