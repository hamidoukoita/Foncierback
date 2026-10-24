package com.example.foncierback.dto;

import com.example.foncierback.entity.enums.EquipementMaison;
import com.example.foncierback.entity.enums.StyleMaison;
import com.example.foncierback.entity.enums.TypeCuisine;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/** Description d'une maison (souhaits de l'acquéreur ou proposition de la société), en requête comme en réponse. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescriptionMaisonDto {

    @Min(value = 1, message = "Le nombre de niveaux est invalide")
    @Max(value = 3, message = "Au plus R+2 (3 niveaux)")
    private Integer nombreNiveaux;

    private StyleMaison style;

    @Min(value = 0, message = "Le nombre de chambres ne peut pas être négatif")
    @Max(value = 30, message = "Le nombre de chambres semble incorrect")
    private Integer nombreChambres;

    @Min(value = 0, message = "Le nombre de chambres avec salle de bain ne peut pas être négatif")
    @Max(value = 30, message = "Le nombre de chambres avec salle de bain semble incorrect")
    private Integer nombreSuites;

    @Min(value = 0, message = "Le nombre de salles de bain ne peut pas être négatif")
    @Max(value = 30, message = "Le nombre de salles de bain semble incorrect")
    private Integer nombreSallesBain;

    @Min(value = 0, message = "Le nombre de salons ne peut pas être négatif")
    @Max(value = 10, message = "Le nombre de salons semble incorrect")
    private Integer nombreSalons;

    private Boolean salleAManger;
    private Boolean toilettesVisiteurs;
    private Boolean magasin;
    private TypeCuisine cuisine;

    @Min(value = 0, message = "Le nombre de places de garage ne peut pas être négatif")
    @Max(value = 10, message = "Le nombre de places de garage semble incorrect")
    private Integer placesGarage;

    private Set<EquipementMaison> equipements;

    @Positive(message = "La surface habitable doit être strictement positive")
    private Double surfaceHabitable;

    @Size(max = 3000, message = "Les précisions ne doivent pas dépasser 3000 caractères")
    private String precisions;
}
