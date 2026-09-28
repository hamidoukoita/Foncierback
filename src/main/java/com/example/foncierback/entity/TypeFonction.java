package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "types_fonction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TypeFonction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé du type de fonction est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Size(max = 50, message = "Le code ne doit pas dépasser 50 caractères")
    @Column(unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_acces_id")
    @ToString.Exclude
    private NiveauAcces niveauAcces;
}
