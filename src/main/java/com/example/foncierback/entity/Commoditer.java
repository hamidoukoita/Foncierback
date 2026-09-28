package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "commodites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commoditer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero
    @Column(name = "distance_km")
    private Double distanceKm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_commodite_id")
    private TypeCommoditer typeCommoditer;
}