package com.example.foncierback.dto.request;

import com.example.foncierback.common.validation.Formats;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangementMotDePasseRequest {

    @NotBlank(message = "Le mot de passe actuel est obligatoire")
    private String ancienMotDePasse;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = Formats.MOT_DE_PASSE_MIN, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
    private String nouveauMotDePasse;
}
