package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutAgrement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "societes_promotrices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocietePromotrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "raison_sociale", nullable = false, length = 150)
    private String raisonSociale;

    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String nif;

    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String rccm;

    @Size(max = 80)
    @Column(name = "numero_agrement", length = 80)
    private String numeroAgrement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_agrement", nullable = false, length = 30)
    @Builder.Default
    private StatutAgrement statutAgrement = StatutAgrement.EN_ATTENTE;

    @Column(length = 500)
    private String logo;

    @Size(max = 255)
    @Column(name = "adresse_siege")
    private String adresseSiege;

    @Size(max = 30)
    private String telephone;

    @Email
    @Size(max = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_kyc")
    private String documentKyc;
}