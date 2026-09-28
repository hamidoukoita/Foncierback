package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.TypeRDV;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date du rendez-vous est obligatoire")
    @Column(name = "date_rendez_vous", nullable = false)
    private LocalDateTime dateRendezVous;

    @NotNull(message = "Le type de rendez-vous est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_rdv", nullable = false, length = 30)
    private TypeRDV typeRDV;

    @NotNull(message = "Le statut du rendez-vous est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutRendezVous statut = StatutRendezVous.EN_ATTENTE;

    @Size(max = 255, message = "Le lieu ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String lieu;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "compte_rendu", columnDefinition = "TEXT")
    private String compteRendu;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquereur_id")
    @ToString.Exclude
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @ToString.Exclude
    private AgentPromoteur agentPromoteur;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id")
    @ToString.Exclude
    private BienFoncier bienFoncier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id")
    @ToString.Exclude
    private Creneau creneau;

    // --- Méthodes de cycle de vie et métier ---

    @PrePersist
    protected void onCreate() {
        if (this.statut == null) {
            this.statut = StatutRendezVous.EN_ATTENTE;
        }
    }

    public void accepter() {
        this.statut = StatutRendezVous.ACCEPTER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = null;
    }

    public void refuser(String motif) {
        this.statut = StatutRendezVous.REFUSER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = motif;
    }
}
