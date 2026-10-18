package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReinitialisationMotDePasseRequest {

    @NotBlank(message = "Le numéro est obligatoire")
    private String telephone;

    @NotBlank(message = "Le code est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code compte 6 chiffres")
    private String code;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = Formats.MOT_DE_PASSE_MIN, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
    private String nouveauMotDePasse;

    public void setTelephone(String valeur) {
        this.telephone = Formats.telephone(valeur);
    }

    public void setCode(String valeur) {
        this.code = valeur == null ? null : valeur.replaceAll("\\s", "");
    }
}
