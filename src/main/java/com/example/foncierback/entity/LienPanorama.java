package com.example.foncierback.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Point de passage d'une visite virtuelle : posé dans un panorama 360° (source),
 * il mène à un autre panorama du même bien (cible), par exemple « Salon → Chambre ».
 * La position est une direction dans la sphère : azimut (-180° à 180°) et élévation (-90° à 90°).
 * La vue d'arrivée, facultative, est la direction regardée en entrant dans la pièce cible.
 */
@Entity
@Table(name = "liens_panoramas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LienPanorama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private MediaFoncier source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cible_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private MediaFoncier cible;

    @Column(nullable = false)
    private Double azimut;

    @Column(nullable = false)
    private Double elevation;

    @Column(length = 60)
    private String libelle;

    @Column(name = "azimut_arrivee")
    private Double azimutArrivee;

    @Column(name = "elevation_arrivee")
    private Double elevationArrivee;
}
