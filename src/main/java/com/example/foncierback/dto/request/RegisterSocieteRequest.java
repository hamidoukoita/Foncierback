package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class RegisterSocieteRequest {

    /** Jackson passe par ce constructeur puis par les setters, qui nettoient la saisie. */
    @JsonCreator
    public RegisterSocieteRequest() {
    }

    // --- Informations Entreprise ---
    @NotBlank(message = "Le nom / raison sociale de la société est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Pattern(regexp = Formats.TEXTE_UTILE, message = "Le nom de la société doit contenir des lettres")
    private String nomSociete;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    @NotBlank(message = "Le téléphone de la société est obligatoire")
    @Size(max = 50, message = "Le téléphone ne doit pas dépasser 50 caractères")
    @Pattern(regexp = Formats.TELEPHONE_MALI, message = Formats.MESSAGE_TELEPHONE)
    private String telephoneSociete;

    @Email(message = "L'email de la société doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String emailSociete;

    private String siteWeb;
    private String description;

    // --- Agréments & Fiscalité DNDC ---
    @NotBlank(message = "Le numéro d'agrément est obligatoire")
    @Size(max = 100, message = "Le numéro d'agrément ne doit pas dépasser 100 caractères")
    @Pattern(regexp = Formats.TEXTE_UTILE, message = "Le numéro d'agrément est invalide")
    private String numeroAgrement;

    @jakarta.validation.constraints.PastOrPresent(message = "La date d'octroi de l'agrément ne peut pas être dans le futur")
    private LocalDate dateAgrement;

    @NotBlank(message = "Le NIF est obligatoire")
    @Size(max = 100, message = "Le NIF ne doit pas dépasser 100 caractères")
    @Pattern(regexp = Formats.TEXTE_UTILE, message = "Le NIF est invalide")
    private String nif;

    private String rccm;

    // --- Responsable Promoteur (Compte de direction) ---
    @NotBlank(message = "Le nom du responsable est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le nom du responsable doit compter entre 2 et 50 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le nom du responsable " + Formats.MESSAGE_NOM)
    private String nomResponsable;

    @NotBlank(message = "Le prénom du responsable est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le prénom du responsable doit compter entre 2 et 50 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le prénom du responsable " + Formats.MESSAGE_NOM)
    private String prenomResponsable;

    @NotBlank(message = "Le téléphone de connexion du responsable est obligatoire")
    @Pattern(regexp = Formats.TELEPHONE_MALI, message = Formats.MESSAGE_TELEPHONE)
    private String telephoneResponsable;

    @Email(message = "L'email du responsable doit être valide")
    private String emailResponsable;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = Formats.MOT_DE_PASSE_MIN, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    // --- Métadonnées KYC ---
    private String documentKycNom;

    // Nettoyage avant validation (Jackson passe par les setters).
    public void setNomSociete(String valeur) {
        this.nomSociete = Formats.nettoyer(valeur);
    }

    public void setAdresse(String valeur) {
        this.adresse = Formats.nettoyer(valeur);
    }

    public void setNumeroAgrement(String valeur) {
        this.numeroAgrement = Formats.nettoyer(valeur);
    }

    public void setNif(String valeur) {
        this.nif = Formats.nettoyer(valeur);
    }

    public void setRccm(String valeur) {
        this.rccm = Formats.nettoyer(valeur);
    }

    public void setNomResponsable(String valeur) {
        this.nomResponsable = Formats.nettoyer(valeur);
    }

    public void setPrenomResponsable(String valeur) {
        this.prenomResponsable = Formats.nettoyer(valeur);
    }

    public void setEmailSociete(String valeur) {
        String email = Formats.nettoyer(valeur);
        this.emailSociete = email == null ? null : email.toLowerCase(java.util.Locale.ROOT);
    }

    public void setEmailResponsable(String valeur) {
        String email = Formats.nettoyer(valeur);
        this.emailResponsable = email == null ? null : email.toLowerCase(java.util.Locale.ROOT);
    }

    public void setSiteWeb(String valeur) {
        this.siteWeb = Formats.nettoyer(valeur);
    }

    public void setDescription(String valeur) {
        this.description = Formats.nettoyer(valeur);
    }

    public void setTelephoneSociete(String valeur) {
        this.telephoneSociete = Formats.telephone(valeur);
    }

    public void setTelephoneResponsable(String valeur) {
        this.telephoneResponsable = Formats.telephone(valeur);
    }
}
