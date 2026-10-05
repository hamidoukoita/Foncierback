package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Ajout d'une visite virtuelle externe (Matterport, Kuula, YouTube 360°…). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaLienRequest {

    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    private String description;

    @NotBlank(message = "Le lien de la visite virtuelle est obligatoire")
    @Size(max = 1000, message = "Le lien ne doit pas dépasser 1000 caractères")
    @Pattern(regexp = "^https://[^\\s]+$", message = "Le lien doit commencer par https://")
    private String lienExterne;
}
