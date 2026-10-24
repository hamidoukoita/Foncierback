package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutProposition;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Proposition de construction faite par la société à l'acquéreur : un modèle du catalogue
 * et/ou une maison décrite pièce par pièce, avec un prix et un délai estimés.
 * L'acquéreur l'accepte (accord conclu) ou la refuse en expliquant pourquoi.
 */
@Entity
@Table(name = "propositions_projets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PropositionProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro d'ordre dans le projet (Proposition n°1, n°2…). */
    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 150)
    private String titre;

    @Embedded
    private DescriptionMaison maison;

    @Column(name = "prix_estime", precision = 15, scale = 2)
    private BigDecimal prixEstime;

    @Column(name = "delai_mois")
    private Integer delaiMois;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutProposition statut = StatutProposition.EN_ATTENTE;

    /** Explication de l'acquéreur quand il refuse la proposition. */
    @Column(name = "reponse_acquereur", columnDefinition = "TEXT")
    private String reponseAcquereur;

    @Column(name = "date_proposition", nullable = false)
    private LocalDateTime dateProposition;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    @ToString.Exclude
    private ProjetConstruction projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_maison_id")
    @ToString.Exclude
    private ModelMaison modelMaison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    @ToString.Exclude
    private Utilisateur auteur;

    @PrePersist
    protected void onCreate() {
        if (dateProposition == null) dateProposition = LocalDateTime.now();
        if (statut == null) statut = StatutProposition.EN_ATTENTE;
    }

    public void repondre(StatutProposition reponse, String commentaire) {
        this.statut = reponse;
        this.reponseAcquereur = commentaire;
        this.dateReponse = LocalDateTime.now();
    }
}
