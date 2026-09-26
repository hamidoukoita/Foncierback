package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutProgramme;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "programmes_fonciers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProgrammeFoncier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du programme est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 200, message = "Le lieu ne doit pas dépasser 200 caractères")
    @Column(length = 200)
    private String lieu;

    @NotBlank(message = "Le numéro du Titre Foncier mère est obligatoire")
    @Size(max = 100, message = "Le numéro du Titre Foncier mère ne doit pas dépasser 100 caractères")
    @Column(name = "numero_titre_mere", nullable = false, length = 100)
    private String numeroTitreMere;

    @NotNull(message = "La superficie totale est obligatoire")
    @Positive(message = "La superficie totale doit être strictement positive")
    @Column(name = "superficie_totale", nullable = false)
    private Double superficieTotale;

    @NotNull(message = "Le statut du programme est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutProgramme statut;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Min(value = 0, message = "L'avancement ne peut pas être inférieur à 0%")
    @Max(value = 100, message = "L'avancement ne peut pas dépasser 100%")
    @Column(nullable = false)
    @Builder.Default
    private Integer avancement = 0;

    @Builder.Default
    @Column(name = "eau_somapep", nullable = false)
    private Boolean eauSomapep = false;

    @Builder.Default
    @Column(name = "electricite_edm", nullable = false)
    private Boolean electriciteEdm = false;

    @Builder.Default
    @Column(name = "voirie_bitumee", nullable = false)
    private Boolean voirieBitumee = false;

    @OneToOne(mappedBy = "programmeFoncier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PlanMasse planMasse;

    @Builder.Default
    @OneToMany(mappedBy = "programmeFoncier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotProgramme> lots = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    private SocietePromotrice societePromotrice;

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.avancement == null) {
            this.avancement = 0;
        }
        if (this.eauSomapep == null) this.eauSomapep = false;
        if (this.electriciteEdm == null) this.electriciteEdm = false;
        if (this.voirieBitumee == null) this.voirieBitumee = false;
    }
}
