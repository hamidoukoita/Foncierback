package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.PlanElementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "plans_masse_elements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlanMasseElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le type de l'élément est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PlanElementType type;

    @Size(max = 120, message = "Le nom ne doit pas dépasser 120 caractères")
    @Column(length = 120)
    private String nom;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String description;

    /** POLYLINE, POLYGON ou POINT encodé en JSON. */
    @Column(name = "geometry_json", columnDefinition = "LONGTEXT", nullable = false)
    private String geometryJson;

    /** Paramètres d'affichage optionnels (icône, taille, couleur, etc.). */
    @Column(name = "style_json", columnDefinition = "LONGTEXT")
    private String styleJson;

    @Builder.Default
    @Column(nullable = false)
    private Boolean visible = true;

    @Builder.Default
    @Column(name = "z_index", nullable = false)
    private Integer zIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_masse_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private PlanMasse planMasse;
}
