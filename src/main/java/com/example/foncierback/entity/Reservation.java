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

    @Column(name = "motif_annulation", columnDefinition = "TEXT")
    private String motifAnnulation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    /** Échéance du décompte : fixée quand l'agent le démarre (null avant). */
    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "date_debut_decompte")
    private LocalDateTime dateDebutDecompte;

    @Column(name = "date_conclusion")
    private LocalDateTime dateConclusion;

    // --- Paiement (simulé : aucun appel à un opérateur) ---

    /** Montant payé pour réserver (prix fixé par la société ; zéro si elle a choisi la gratuité). */
    @Column(name = "montant_reservation", precision = 15, scale = 2)
    private java.math.BigDecimal montantReservation;

    @Column(name = "moyen_paiement", length = 30)
    private String moyenPaiement;

    @Column(name = "reference_paiement", length = 40)
    private String referencePaiement;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Builder.Default
    @Column(name = "rembourse", nullable = false, columnDefinition = "boolean default false")
    private Boolean rembourse = Boolean.FALSE;

    @Column(name = "date_remboursement")
    private LocalDateTime dateRemboursement;


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

    /** Réservation encore en vigueur : elle bloque le bien. */
    public boolean estActive() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.EN_COURS;
    }

    /** L'agent démarre le décompte : la réservation court jusqu'à maintenant + durée. */
    public void demarrerDecompte(int dureeHeures) {
        LocalDateTime maintenant = LocalDateTime.now();
        this.statut = StatutReservation.EN_COURS;
        this.dateDebutDecompte = maintenant;
        this.dateExpiration = maintenant.plusHours(dureeHeures);
        this.dateTraitement = maintenant;
    }

    public void conclureVente() {
        this.statut = StatutReservation.VENDUE;
        this.dateConclusion = LocalDateTime.now();
        this.dateTraitement = this.dateConclusion;
    }

    /** Échéance atteinte sans vente : le montant payé n'est pas rendu. */
    public void expirer() {
        this.statut = StatutReservation.EXPIREE;
        this.dateTraitement = LocalDateTime.now();
        this.motifAnnulation = "Délai de réservation écoulé sans vente : le montant de la réservation n'est pas remboursé.";
    }

    /** Désistement de l'acquéreur : jamais remboursé. */
    public void annulerParAcquereur() {
        this.statut = StatutReservation.ANNULEE;
        this.dateTraitement = LocalDateTime.now();
        this.motifAnnulation = "Annulée par l'acquéreur : le montant de la réservation n'est pas remboursé.";
        this.rembourse = Boolean.FALSE;
    }

    /** Le bien a été vendu à un autre acquéreur : la réservation ne peut plus aboutir, elle est remboursée. */
    public void annulerAvecRemboursement(String motif) {
        LocalDateTime maintenant = LocalDateTime.now();
        this.statut = StatutReservation.ANNULEE;
        this.dateTraitement = maintenant;
        this.motifAnnulation = motif;
        boolean payee = montantReservation != null && montantReservation.signum() > 0;
        this.rembourse = payee;
        this.dateRemboursement = payee ? maintenant : null;
    }
}
