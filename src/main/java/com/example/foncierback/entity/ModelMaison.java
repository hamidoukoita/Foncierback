package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modeles_maison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ModelMaison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(name = "libeller", unique = true, nullable = false, length = 100)
    private String libeller;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // --- Cardinalités / Relations ---

    @Builder.Default
    @OneToMany(mappedBy = "modelMaison")
    @ToString.Exclude
    private List<ProjetConstruction> projetsConstruction = new ArrayList<>();
}
