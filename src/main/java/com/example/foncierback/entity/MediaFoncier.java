package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.TypeMedia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Média (photo, panorama 360° ou lien de visite virtuelle) rattaché à un programme foncier
 * OU à un bien foncier (lot de programme, parcelle individuelle).
 * PHOTO et PANORAMA_360 : fichier stocké sur disque (nomFichier).
 * VISITE_VIRTUELLE : lien externe https (lienExterne).
 */
@Entity
@Table(name = "medias_fonciers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MediaFoncier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeMedia type;

    @Column(length = 150)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "type_contenu", length = 100)
    private String typeContenu;

    @Column(name = "lien_externe", length = 1000)
    private String lienExterne;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private ProgrammeFoncier programmeFoncier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private BienFoncier bienFoncier;

    @PrePersist
    void avantCreation() {
        if (dateAjout == null) {
            dateAjout = LocalDateTime.now();
        }
        if (ordre == null) {
            ordre = 1;
        }
    }
}
