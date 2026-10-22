package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Maison clé en main : une maison construite (ou livrée prête à habiter) vendue par une société,
 * à une adresse précise. Bien foncier à part entière : réservation, rendez-vous, médias (photos, 360°),
 * localisation et commodités fonctionnent comme pour les lots et les parcelles.
 * La superficie héritée est celle du terrain ; la surface habitable est propre à la maison.
 */
@Entity
@Table(name = "maisons_cle_en_main")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MaisonCleEnMain extends BienFoncier {

    @NotBlank(message = "Le titre de la maison est obligatoire")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    @Column(nullable = false, length = 150)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @PositiveOrZero
    @Column(name = "nombre_chambres")
    private Integer nombreChambres;

    @PositiveOrZero
    @Column(name = "nombre_salles_bain")
    private Integer nombreSallesBain;

    @PositiveOrZero
    @Column(name = "nombre_niveaux")
    private Integer nombreNiveaux;

    @PositiveOrZero
    @Column(name = "surface_habitable")
    private Double surfaceHabitable;

    @Size(max = 100)
    @Column(name = "numero_titre_foncier", length = 100)
    private String numeroTitreFoncier;

    @Size(max = 255)
    @Column(length = 255)
    private String adresse;

    /** Équipements mis en avant : garage, jardin, cuisine équipée… */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "maisons_equipements", joinColumns = @JoinColumn(name = "maison_id"))
    @Column(name = "equipement", length = 80)
    @ToString.Exclude
    private List<String> equipements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id", nullable = false)
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    /** Panorama par lequel commence la visite virtuelle (null : le premier de la galerie). */
    @Column(name = "panorama_depart_id")
    private Long panoramaDepartId;

    @Override
    public SocietePromotrice societeGestionnaire() {
        return societePromotrice;
    }

    @Override
    public String designation() {
        return titre;
    }

    @Override
    public String typeBien() {
        return "MAISON_CLE_EN_MAIN";
    }

}
