package com.example.foncierback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lots_programmes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LotProgramme extends BienFoncier {

    @NotBlank(message = "Le numéro de lot est obligatoire")
    @Size(max = 50, message = "Le numéro de lot ne doit pas dépasser 50 caractères")
    @Column(name = "numero_lot", nullable = false, length = 50)
    private String numeroLot;

    @NotBlank(message = "Le numéro d'îlot est obligatoire")
    @Size(max = 50, message = "Le numéro d'îlot ne doit pas dépasser 50 caractères")
    @Column(name = "numero_ilot", nullable = false, length = 50)
    private String numeroIlot;

    @Size(max = 100, message = "Le numéro d'îlot de lotissement ne doit pas dépasser 100 caractères")
    @Column(name = "numero_ilot_lotissement", length = 100)
    private String numeroIlotLotissement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    @JsonIgnore
    private ProgrammeFoncier programmeFoncier;

}
