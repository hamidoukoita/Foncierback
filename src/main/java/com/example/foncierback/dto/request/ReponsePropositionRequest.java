package com.example.foncierback.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Réponse de l'acquéreur à une proposition ; le commentaire explique un refus. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponsePropositionRequest {

    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String commentaire;
}
