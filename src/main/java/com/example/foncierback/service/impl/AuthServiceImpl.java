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
import com.example.foncierback.dto.request.RegisterSocieteRequest;
import com.example.foncierback.dto.response.SocietePromotriceResponse;
import com.example.foncierback.entity.AgentPromoteur;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;
import com.example.foncierback.repository.AgentPromoteurRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AcquereurService acquereurService;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final AgentPromoteurRepository agentPromoteurRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        Utilisateur user = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new BadRequestException("Téléphone ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new BadRequestException("Téléphone ou mot de passe incorrect");
        }

        if (user.getStatut() == StatutCompte.SUSPENDU || user.getStatut() == StatutCompte.DESACTIVE) {
            throw new BadRequestException("Votre compte est actuellement suspendu ou désactivé. Veuillez contacter le support.");
        }

        if (user.getStatut() != StatutCompte.ACTIF) {
            throw new BadRequestException("Compte non actif (statut: " + user.getStatut() + ")");
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
                .permissions(userDetails.getPermissions())
                .societeId(userDetails.getSocieteId())
                .societeNom(userDetails.getSocieteNom())
                .estResponsableSociete(user instanceof AgentPromoteur agent ? agent.isEstResponsableSociete() : null)
                .fonctionLibelle(userDetails.getFonctionLibelle())
                .niveauAccesCode(userDetails.getNiveauAccesCode())
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

    @Override
    @Transactional
    public SocietePromotriceResponse registerSociete(RegisterSocieteRequest request) {
        if (societePromotriceRepository.existsByNom(request.getNomSociete())) {
            throw new BadRequestException("Une société promotrice avec le nom '" + request.getNomSociete() + "' existe déjà");
        }
        if (request.getNif() != null && !request.getNif().isBlank() && societePromotriceRepository.existsByNif(request.getNif())) {
            throw new BadRequestException("Une société promotrice avec le NIF '" + request.getNif() + "' existe déjà");
        }
        if (request.getNumeroAgrement() != null && !request.getNumeroAgrement().isBlank() && societePromotriceRepository.existsByNumeroAgrement(request.getNumeroAgrement())) {
            throw new BadRequestException("Une société avec le numéro d'agrément '" + request.getNumeroAgrement() + "' existe déjà");
        }
        if (utilisateurRepository.existsByTelephone(request.getTelephoneResponsable())) {
            throw new BadRequestException("Un compte utilisateur avec le téléphone " + request.getTelephoneResponsable() + " existe déjà");
        }

        // 1. Création de l'entité Société Promotrice (Agrément en attente par défaut)
        SocietePromotrice societe = SocietePromotrice.builder()
                .nom(request.getNomSociete())
                .adresse(request.getAdresse())
                .telephone(request.getTelephoneSociete())
                .email(request.getEmailSociete())
                .nif(request.getNif())
                .numeroAgrement(request.getNumeroAgrement())
                .dateAgrement(request.getDateAgrement() != null ? request.getDateAgrement() : LocalDate.now())
                .statutAgrement(StatutAgrement.EN_ATTENTE)
                .siteWeb(request.getSiteWeb())
                .description(request.getDescription())
                .build();

        SocietePromotrice savedSociete = societePromotriceRepository.save(societe);

        // 2. Création de l'Agent Promoteur Responsable (Directeur / Mandataire)
        AgentPromoteur responsable = new AgentPromoteur();
        responsable.setNom(request.getNomResponsable());
        responsable.setPrenom(request.getPrenomResponsable());
        responsable.setTelephone(request.getTelephoneResponsable());
        responsable.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        responsable.setStatut(StatutCompte.ACTIF);
        responsable.setDateCreation(java.time.LocalDateTime.now());
        responsable.setEstResponsableSociete(true);
        responsable.setDateAffectation(LocalDate.now());
        responsable.setSocietePromotrice(savedSociete);

        agentPromoteurRepository.save(responsable);

        return SocietePromotriceResponse.builder()
                .id(savedSociete.getId())
                .nom(savedSociete.getNom())
                .adresse(savedSociete.getAdresse())
                .telephone(savedSociete.getTelephone())
                .email(savedSociete.getEmail())
                .nif(savedSociete.getNif())
                .numeroAgrement(savedSociete.getNumeroAgrement())
                .dateAgrement(savedSociete.getDateAgrement())
                .statutAgrement(savedSociete.getStatutAgrement())
                .siteWeb(savedSociete.getSiteWeb())
                .description(savedSociete.getDescription())
                .build();
    }
}
