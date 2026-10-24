package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageProjetRequest {

    @NotBlank(message = "Le message est vide")
    @Size(max = 2000, message = "Le message ne doit pas dépasser 2000 caractères")
    private String texte;
}
