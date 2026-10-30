package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import com.example.foncierback.common.validation.Telephone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Nouveau numéro de connexion, confirmé par le mot de passe actuel. */
@Data
@NoArgsConstructor
public class ChangementTelephoneRequest {

    @NotBlank(message = "Le nouveau numéro est obligatoire")
    @Telephone
    private String nouveauTelephone;

    @NotBlank(message = "Le mot de passe actuel est obligatoire")
    private String motDePasse;

    public void setNouveauTelephone(String valeur) {
        this.nouveauTelephone = Formats.telephone(valeur);
    }
}
