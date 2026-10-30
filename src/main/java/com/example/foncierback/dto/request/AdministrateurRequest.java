package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;

import com.example.foncierback.entity.enums.StatutCompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrateurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le nom doit compter entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = Formats.NOM_MIN, max = Formats.NOM_MAX, message = "Le prénom doit compter entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    @com.example.foncierback.common.validation.Telephone(maliUniquement = true)
    private String telephone;

    private String motDePasse;

    private StatutCompte statut;

    public void setTelephone(String valeur) {
        this.telephone = com.example.foncierback.common.validation.Formats.telephone(valeur);
    }
}
