package com.example.foncierback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "modeles_maison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelMaison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    @Column(name = "libeller", unique = true, nullable = false)
    private String libeller;
}
