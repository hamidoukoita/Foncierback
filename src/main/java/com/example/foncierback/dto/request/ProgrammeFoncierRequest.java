package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutProgramme;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeFoncierRequest {

    @NotBlank(message = "Le nom du programme est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nom;

    private String description;

    @Size(max = 200, message = "Le lieu ne doit pas dépasser 200 caractères")
    private String lieu;

    @NotBlank(message = "Le numéro du Titre Foncier mère est obligatoire")
    @Size(max = 100, message = "Le numéro du Titre Foncier mère ne doit pas dépasser 100 caractères")
    private String numeroTitreMere;

    @NotNull(message = "La superficie totale est obligatoire")
    @Positive(message = "La superficie totale doit être strictement positive")
    private Double superficieTotale;

    @NotNull(message = "Le statut du programme est obligatoire")
    private StatutProgramme statut;

    @Min(value = 0, message = "L'avancement ne peut pas être inférieur à 0%")
    @Max(value = 100, message = "L'avancement ne peut pas dépasser 100%")
    @Builder.Default
    private Integer avancement = 0;

    @Builder.Default
    private Boolean eauSomapep = false;

    @Builder.Default
    private Boolean electriciteEdm = false;

    @Builder.Default
    private Boolean voirieBitumee = false;

    private Long societeId;
}
