package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutAgrement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocietePromotriceRequest {

    @NotBlank(message = "Le nom de la société est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nom;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    private String telephone;

    @Email(message = "L'adresse email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @Size(max = 100, message = "Le NIF ne doit pas dépasser 100 caractères")
    private String nif;

    @Size(max = 100, message = "Le numéro d'agrément ne doit pas dépasser 100 caractères")
    private String numeroAgrement;

    private LocalDate dateAgrement;

    private StatutAgrement statutAgrement;

    private String logoUrl;

    private String siteWeb;

    private String description;
}
