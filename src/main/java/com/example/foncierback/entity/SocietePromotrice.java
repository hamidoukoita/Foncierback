package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutAgrement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "societes_promotrices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SocietePromotrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la société est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Column(nullable = false, unique = true, length = 150)
    private String nom;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    @Column(nullable = false, length = 50)
    private String telephone;

    @Email(message = "L'adresse email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    @Column(unique = true, length = 100)
    private String email;

    @Size(max = 100, message = "Le NIF ne doit pas dépasser 100 caractères")
    @Column(name = "nif", unique = true, length = 100)
    private String nif;

    @Size(max = 100, message = "Le numéro d'agrément ne doit pas dépasser 100 caractères")
    @Column(name = "numero_agrement", unique = true, length = 100)
    private String numeroAgrement;

    @Column(name = "date_agrement")
    private LocalDate dateAgrement;

    @NotNull(message = "Le statut de l'agrément est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_agrement", nullable = false, length = 30)
    @Builder.Default
    private StatutAgrement statutAgrement = StatutAgrement.EN_ATTENTE;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Cardinalités / Relations ---

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AgentPromoteur> agents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProgrammeFoncier> programmes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ParcelleIndividuelle> parcellesIndividuelles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProjetConstruction> projetsConstruction = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Creneau> creneaux = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "societePromotrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DocumentKyc> documentsKyc = new ArrayList<>();
}
