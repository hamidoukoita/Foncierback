package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Mot de passe oublié : numéro du compte auquel « envoyer » le code (SMS simulé). */
@Data
@NoArgsConstructor
public class DemandeCodeRequest {

    @NotBlank(message = "Le numéro est obligatoire")
    private String telephone;

    public void setTelephone(String valeur) {
        this.telephone = Formats.telephone(valeur);
    }
}
