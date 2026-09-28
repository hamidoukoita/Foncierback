package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 100, message = "Le numéro de dossier ne doit pas dépasser 100 caractères")
    @Column(name = "numero_dossier", nullable = false, unique = true, length = 100)
    private String numeroDossier;

    @NotNull(message = "La date de réservation est obligatoire")
    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @NotNull(message = "Le statut de la réservation est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id", nullable = false)
    @ToString.Exclude
    private BienFoncier bienFoncier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquereur_id")
    @ToString.Exclude
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @ToString.Exclude
    private AgentPromoteur agentPromoteur;



    // --- Méthodes de cycle de vie et métier ---

    @PrePersist
    protected void onCreate() {
        if (this.dateReservation == null) {
            this.dateReservation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutReservation.EN_ATTENTE;
        }
    }

    public void confirmer() {
        this.statut = StatutReservation.CONFIRMER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = null;
    }

    public void refuser(String motif) {
        this.statut = StatutReservation.REFUSER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = motif;
    }
}
