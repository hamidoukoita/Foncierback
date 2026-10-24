package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Modification du nom et du prénom par le titulaire du compte. */
@Data
@AllArgsConstructor
public class IdentiteRequest {

    /** Jackson passe par ce constructeur puis par les setters, qui nettoient la saisie. */
    @JsonCreator
    public IdentiteRequest() {
    }

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le nom doit compter entre 2 et 50 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le nom " + Formats.MESSAGE_NOM)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le prénom doit compter entre 2 et 50 caractères")
    @Pattern(regexp = Formats.NOM_PERSONNE, message = "Le prénom " + Formats.MESSAGE_NOM)
    private String prenom;

    // Nettoyage avant validation (Jackson passe par les setters).
    public void setNom(String valeur) {
        this.nom = Formats.nettoyer(valeur);
    }

    public void setPrenom(String valeur) {
        this.prenom = Formats.nettoyer(valeur);
    }
}
