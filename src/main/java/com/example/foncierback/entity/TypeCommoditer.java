package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "types_commodite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TypeCommoditer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé du type de commodité est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    @Column(length = 255)
    private String description;

    // --- Cardinalités / Relations ---

    @Builder.Default
    @OneToMany(mappedBy = "typeCommodite", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Commoditer> commodites = new ArrayList<>();
}
