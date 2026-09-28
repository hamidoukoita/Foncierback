package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la notification est obligatoire")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "Le message de la notification est obligatoire")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lue = false;

    @NotNull(message = "La date d'envoi est obligatoire")
    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_notification_id")
    @ToString.Exclude
    private TypeNotification typeNotification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @ToString.Exclude
    private Utilisateur utilisateur;

    @PrePersist
    protected void onCreate() {
        if (this.dateEnvoi == null) {
            this.dateEnvoi = LocalDateTime.now();
        }
        if (this.lue == null) {
            this.lue = false;
        }
    }
}
