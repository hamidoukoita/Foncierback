package com.example.foncierback.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterSocieteRequest {

    // --- Informations Entreprise ---
    @NotBlank(message = "Le nom / raison sociale de la société est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nomSociete;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    @NotBlank(message = "Le téléphone de la société est obligatoire")
    @Size(max = 50, message = "Le téléphone ne doit pas dépasser 50 caractères")
    private String telephoneSociete;

    @Email(message = "L'email de la société doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String emailSociete;

    private String siteWeb;
    private String description;

    // --- Agréments & Fiscalité DNDC ---
    @NotBlank(message = "Le numéro d'agrément est obligatoire")
    @Size(max = 100, message = "Le numéro d'agrément ne doit pas dépasser 100 caractères")
    private String numeroAgrement;

    private LocalDate dateAgrement;

    @NotBlank(message = "Le NIF est obligatoire")
    @Size(max = 100, message = "Le NIF ne doit pas dépasser 100 caractères")
    private String nif;

    private String rccm;

    // --- Responsable Promoteur (Compte de direction) ---
    @NotBlank(message = "Le nom du responsable est obligatoire")
    private String nomResponsable;

    @NotBlank(message = "Le prénom du responsable est obligatoire")
    private String prenomResponsable;

    @NotBlank(message = "Le téléphone de connexion du responsable est obligatoire")
    private String telephoneResponsable;

    @Email(message = "L'email du responsable doit être valide")
    private String emailResponsable;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    private String motDePasse;

    // --- Métadonnées KYC ---
    private String documentKycNom;
}
