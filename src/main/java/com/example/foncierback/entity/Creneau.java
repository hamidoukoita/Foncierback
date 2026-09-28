package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "creneaux")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'heure ou le libellé du créneau est obligatoire")
    @Size(max = 50, message = "Le libellé de l'heure ne doit pas dépasser 50 caractères")
    @Column(nullable = false, length = 50)
    private String heure;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "heure_fin")
    private LocalTime heureFin;

    @Builder.Default
    @Column(nullable = false)
    private Boolean disponible = true;

    // --- Cardinalités / Relations ---

    @NotNull(message = "La société promotrice est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id", nullable = false)
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    @Builder.Default
    @OneToMany(mappedBy = "creneau", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RendezVous> rendezVous = new ArrayList<>();
}
