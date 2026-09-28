package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutProgramme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeFoncierResponse {

    private Long id;
    private String nom;
    private String description;
    private String lieu;
    private String numeroTitreMere;
    private Double superficieTotale;
    private StatutProgramme statut;
    private LocalDateTime dateCreation;
    private Integer avancement;
    private Boolean eauSomapep;
    private Boolean electriciteEdm;
    private Boolean voirieBitumee;
    private Long societeId;
    private String societeNom;
    private Integer totalLots;
    private Long planMasseId;
}
