package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Rattachement d'une commodité de proximité à une cité (programme) ou à un bien
 * (parcelle individuelle), avec la distance déclarée par la société promotrice.
 * Exactement une cible : programme OU bien.
 */
@Entity
@Table(name = "proximites_commodites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProximiteCommodite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Distance déclarée en kilomètres ; si absente, elle est calculée depuis les coordonnées GPS. */
    @DecimalMin(value = "0.0", message = "La distance ne peut pas être négative")
    @Column(name = "distance_km")
    private Double distanceKm;

    // --- Cardinalités / Relations ---

    @NotNull(message = "La commodité est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commodite_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Commoditer commodite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private ProgrammeFoncier programmeFoncier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private BienFoncier bienFoncier;
}
