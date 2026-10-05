package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.TypeDocumentKyc;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents_kyc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    @Column(name = "nom_fichier_genere", nullable = false, unique = true)
    private String nomFichierGenere;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false)
    private TypeDocumentKyc typeDocument;

    @Column(name = "type_mime", nullable = false)
    private String typeMime;

    @Column(name = "taille_fichier", nullable = false)
    private Long tailleFichier;

    @Column(name = "chemin_acces", nullable = false)
    private String cheminAcces;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_promotrice_id", nullable = false)
    private SocietePromotrice societePromotrice;
}
