package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "niveaux_acces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NiveauAcces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé du niveau d'accès est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Size(max = 50, message = "Le code ne doit pas dépasser 50 caractères")
    @Column(unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Cardinalités / Relations ---

    @Builder.Default
    @OneToMany(mappedBy = "niveauAcces", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TypeFonction> typeFonctions = new ArrayList<>();
}
