package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.Pattern;
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
public class AgentPromoteurRequest {

    /** Jackson passe par ce constructeur puis par les setters, qui nettoient la saisie. */
    @JsonCreator
    public AgentPromoteurRequest() {
    }

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le nom " + Formats.MESSAGE_NOM)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le prénom " + Formats.MESSAGE_NOM)
    private String prenom;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    @Pattern(regexp = Formats.TELEPHONE_MALI, message = Formats.MESSAGE_TELEPHONE)
    private String telephone;

    @Size(min = Formats.MOT_DE_PASSE_MIN, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    private StatutCompte statut;

    private Boolean estResponsableSociete;

    private LocalDate dateAffectation;

    private Long societeId;

    private Long typeFonctionId;

    // Nettoyage avant validation (Jackson passe par les setters).
    public void setNom(String valeur) {
        this.nom = Formats.nettoyer(valeur);
    }

    public void setPrenom(String valeur) {
        this.prenom = Formats.nettoyer(valeur);
    }

    public void setTelephone(String valeur) {
        this.telephone = Formats.telephone(valeur);
    }
}
