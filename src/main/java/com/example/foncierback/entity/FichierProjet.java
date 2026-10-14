package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.TypeFichierProjet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Pièce jointe d'un projet (Titre Foncier, plan ou photo du modèle souhaité).
 * Stockée hors des dossiers publics : téléchargement uniquement via l'API, avec contrôle d'accès.
 */
@Entity
@Table(name = "fichiers_projets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FichierProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeFichierProjet type;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "content_type", length = 100)
    private String contentType;

    private Long taille;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    @ToString.Exclude
    private ProjetConstruction projet;
}
