package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commodites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Commoditer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la commodité est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @Size(max = 100, message = "L'icône ne doit pas dépasser 100 caractères")
    @Column(name = "icone", length = 100)
    private String icone;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String description;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_commodite_id")
    @ToString.Exclude
    private TypeCommoditer typeCommodite;

    @Builder.Default
    @ManyToMany(mappedBy = "commodites")
    @ToString.Exclude
    private List<BienFoncier> biens = new ArrayList<>();
}
