package com.example.foncierback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "parcelles_individuelles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ParcelleIndividuelle extends BienFoncier {

    @NotBlank(message = "Le numéro de Titre Foncier est obligatoire")
    @Size(max = 100, message = "Le numéro de Titre Foncier ne doit pas dépasser 100 caractères")
    @Column(name = "numero_titre_foncier", nullable = false, length = 100)
    private String numeroTitreFoncier;

    @Builder.Default
    @Column(name = "mur_cloture", nullable = false)
    private Boolean murCloture = false;

    @Builder.Default
    @Column(name = "eau_somapep", nullable = false)
    private Boolean eauSomapep = false;

    @Builder.Default
    @Column(name = "electricite_edm", nullable = false)
    private Boolean electriciteEdm = false;

    @Builder.Default
    @Column(name = "voie_bitumee", nullable = false)
    private Boolean voieBitumee = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    private SocietePromotrice societePromotrice;
}
