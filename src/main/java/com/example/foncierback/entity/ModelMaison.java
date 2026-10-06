package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modeles_maison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ModelMaison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(name = "libeller", unique = true, nullable = false, length = 100)
    private String libeller;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "plan_url", length = 1000)
    private String planUrl;

    @PositiveOrZero(message = "La surface minimale ne peut pas être négative")
    @Column(name = "surface_terrain_min")
    private Double surfaceTerrainMin;

    @PositiveOrZero(message = "La surface maximale ne peut pas être négative")
    @Column(name = "surface_terrain_max")
    private Double surfaceTerrainMax;

    @PositiveOrZero(message = "La surface construite ne peut pas être négative")
    @Column(name = "surface_construite")
    private Double surfaceConstruite;

    @PositiveOrZero(message = "Le nombre de chambres ne peut pas être négatif")
    @Column(name = "nombre_chambres")
    private Integer nombreChambres;

    @PositiveOrZero(message = "Le nombre de salles de bain ne peut pas être négatif")
    @Column(name = "nombre_salles_bain")
    private Integer nombreSallesBain;

    /**
     * Type/forme de terrain compatible (ex. STANDARD, ANGLE, IRREGULIER).
     * Le champ reste libre pour ne pas figer prématurément le catalogue métier.
     */
    @Size(max = 50, message = "Le type de terrain ne doit pas dépasser 50 caractères")
    @Column(name = "type_terrain_compatible", length = 50)
    private String typeTerrainCompatible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    @Builder.Default
    @OneToMany(mappedBy = "modelMaison")
    @ToString.Exclude
    private List<ProjetConstruction> projetsConstruction = new ArrayList<>();
}
