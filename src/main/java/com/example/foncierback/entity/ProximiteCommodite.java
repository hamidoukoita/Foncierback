package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

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

    /** Distance en kilomètres entre la commodité et le programme / bien. */
    @DecimalMin(value = "0.0", message = "La distance ne peut pas être négative")
    @Column(name = "distance_km")
    private Double distanceKm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commodite_id", nullable = false)
    @ToString.Exclude
    private Commoditer commodite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id")
    @ToString.Exclude
    private ProgrammeFoncier programmeFoncier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id")
    @ToString.Exclude
    private BienFoncier bienFoncier;
}
