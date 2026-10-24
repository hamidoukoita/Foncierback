package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutProjet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

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

    @Size(max = 50, message = "Le type de terrain ne doit pas dépasser 50 caractères")
    @Column(name = "type_terrain", length = 50)
    private String typeTerrain;

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Modèle personnalisé (quand l'acquéreur ne choisit pas un modèle du catalogue) ---
    // Les colonnes historiques (chambres, salles de bain, surface, description) sont conservées.

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nombreChambres", column = @Column(name = "nombre_chambres_souhaitees")),
            @AttributeOverride(name = "nombreSallesBain", column = @Column(name = "nombre_salles_bain_souhaitees")),
            @AttributeOverride(name = "surfaceHabitable", column = @Column(name = "surface_construite_souhaitee")),
            @AttributeOverride(name = "precisions",
                    column = @Column(name = "description_modele_personnalise", columnDefinition = "TEXT"))
    })
    private DescriptionMaison maisonSouhaitee;

    @Column(name = "budget_estime", precision = 15, scale = 2)
    private BigDecimal budgetEstime;

    @Column(name = "document_tf_url", length = 1000)
    private String documentTfUrl;

    @NotNull(message = "Le statut du projet est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutProjet statut = StatutProjet.EN_ETUDE;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    @Builder.Default
    private Integer progression = 0;

    @Size(max = 100)
    @Column(name = "etape_avancement", length = 100)
    private String etapeAvancement;

    @Column(name = "commentaire_avancement", columnDefinition = "TEXT")
    private String commentaireAvancement;

    @Column(name = "date_derniere_mise_a_jour")
    private LocalDateTime dateDerniereMiseAJour;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @NotNull(message = "La date de la demande est obligatoire")
    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquereur_id")
    @ToString.Exclude
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_maison_id")
    @ToString.Exclude
    private ModelMaison modelMaison;

    /** Maison clé en main prise comme référence (« je veux une maison comme celle-ci »). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maison_reference_id")
    @ToString.Exclude
    private MaisonCleEnMain maisonReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @ToString.Exclude
    private AgentPromoteur agentPromoteur;

    @Builder.Default
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private java.util.List<FichierProjet> fichiers = new java.util.ArrayList<>();

    /** Proposition de la société acceptée par l'acquéreur : elle fixe les termes de l'accord. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposition_retenue_id")
    @ToString.Exclude
    private PropositionProjet propositionRetenue;

    /** Une réponse (accord, refus, proposition) n'est possible que tant que le dossier est en cours. */
    public boolean isEnCours() {
        return statut == StatutProjet.EN_ETUDE || statut == StatutProjet.EN_DISCUSSION;
    }

    public boolean isModelePersonnalise() {
        return modelMaison == null && maisonReference == null;
    }

    @PrePersist
    protected void onCreate() {
        if (this.dateDemande == null) this.dateDemande = LocalDateTime.now();
        if (this.statut == null) this.statut = StatutProjet.EN_ETUDE;
        if (this.progression == null) this.progression = 0;
        if (this.dateDerniereMiseAJour == null) this.dateDerniereMiseAJour = LocalDateTime.now();
        if (this.etapeAvancement == null || this.etapeAvancement.isBlank()) this.etapeAvancement = "Dossier reçu";
    }

    /** L'acquéreur retient une proposition : l'accord est conclu. */
    public void accepterProposition(PropositionProjet proposition) {
        this.propositionRetenue = proposition;
        if (proposition.getModelMaison() != null) this.modelMaison = proposition.getModelMaison();
        validerProjet();
    }

    public void validerProjet() {
        this.statut = StatutProjet.ACCEPTER;
        this.dateTraitement = LocalDateTime.now();
        this.dateDerniereMiseAJour = this.dateTraitement;
        this.motifRefus = null;
        if (this.progression < 100 && this.progression == 0) this.progression = 25;
        this.etapeAvancement = "Projet accepté";
    }

    public void refuserProjet(String motif) {
        this.statut = StatutProjet.REFUSER;
        this.dateTraitement = LocalDateTime.now();
        this.dateDerniereMiseAJour = this.dateTraitement;
        this.motifRefus = motif;
        this.etapeAvancement = "Projet refusé";
    }

    public void mettreAJourAvancement(Integer progression, String etape, String commentaire) {
        this.progression = progression;
        this.etapeAvancement = (etape == null || etape.isBlank()) ? this.etapeAvancement : etape.trim();
        this.commentaireAvancement = commentaire;
        this.dateDerniereMiseAJour = LocalDateTime.now();
    }
}
