package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    /** Le numéro est comparé sans espaces ni indicatif. */
    public void setTelephone(String valeur) {
        this.telephone = com.example.foncierback.common.validation.Formats.telephone(valeur);
    }
}
