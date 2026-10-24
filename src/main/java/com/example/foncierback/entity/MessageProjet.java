package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.CoteEchange;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** Message libre du fil d'échange entre l'acquéreur et la société, à propos d'un projet de construction. */
@Entity
@Table(name = "messages_projets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texte;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private CoteEchange cote;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    @ToString.Exclude
    private ProjetConstruction projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    @ToString.Exclude
    private Utilisateur auteur;

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) dateEnvoi = LocalDateTime.now();
    }
}
