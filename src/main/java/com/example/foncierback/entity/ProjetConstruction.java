package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutProjet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projets_construction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjetConstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de dossier est obligatoire")
    @Size(max = 100, message = "Le numéro de dossier ne doit pas dépasser 100 caractères")
    @Column(name = "numero_dossier", nullable = false, unique = true, length = 100)
    private String numeroDossier;

    @NotBlank(message = "Le numéro du Titre Foncier est obligatoire")
    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    @Column(name = "numero_titre_foncier", nullable = false, length = 100)
    private String numeroTitreFoncier;

    @Size(max = 200, message = "La localisation ne doit pas dépasser 200 caractères")
    @Column(name = "localisation_terrain", length = 200)
    private String localisationTerrain;

    @NotNull(message = "La superficie du terrain est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    @Column(name = "superficie_terrain", nullable = false)
    private Double superficieTerrain;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "budget_estime", precision = 15, scale = 2)
    private BigDecimal budgetEstime;

    @Column(name = "document_tf_url")
    private String documentTfUrl;

    @NotNull(message = "Le statut du projet est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutProjet statut = StatutProjet.EN_ETUDE;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @NotNull(message = "La date de la demande est obligatoire")
    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquereur_id")
    @ToString.Exclude
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_maison_id")
    @ToString.Exclude
    private ModelMaison modelMaison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @ToString.Exclude
    private AgentPromoteur agentPromoteur;

    // --- Méthodes de cycle de vie et métier ---

    @PrePersist
    protected void onCreate() {
        if (this.dateDemande == null) {
            this.dateDemande = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutProjet.EN_ETUDE;
        }
    }

    public void validerProjet() {
        this.statut = StatutProjet.ACCEPTER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = null;
    }

    public void refuserProjet(String motif) {
        this.statut = StatutProjet.REFUSER;
        this.dateTraitement = LocalDateTime.now();
        this.motifRefus = motif;
    }
}
