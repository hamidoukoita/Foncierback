package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Nouveau numéro de connexion, confirmé par le mot de passe actuel. */
@Data
@NoArgsConstructor
public class ChangementTelephoneRequest {

    @NotBlank(message = "Le nouveau numéro est obligatoire")
    @Pattern(regexp = Formats.TELEPHONE_MALI, message = Formats.MESSAGE_TELEPHONE)
    private String nouveauTelephone;

    @NotBlank(message = "Le mot de passe actuel est obligatoire")
    private String motDePasse;

    public void setNouveauTelephone(String valeur) {
        this.nouveauTelephone = Formats.telephone(valeur);
    }
}
