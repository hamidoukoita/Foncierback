package com.example.foncierback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans_masse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlanMasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le plan est obligatoire")
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String plan;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String description;

    @Size(max = 50, message = "La version ne doit pas dépasser 50 caractères")
    @Column(length = 50)
    private String version;

    @NotNull(message = "La date de mise à jour est obligatoire")
    @Column(name = "date_mise_a_jour", nullable = false)
    private LocalDateTime dateMiseAJour;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false, unique = true)
    @JsonIgnore
    private ProgrammeFoncier programmeFoncier;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (this.dateMiseAJour == null) {
            this.dateMiseAJour = LocalDateTime.now();
        }
    }
}
