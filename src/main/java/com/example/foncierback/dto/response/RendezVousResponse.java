package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.TypeRDV;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousResponse {

    private Long id;
    private LocalDateTime dateRendezVous;
    private TypeRDV typeRDV;
    private StatutRendezVous statut;
    private String lieu;
    private String motifRefus;
    private String compteRendu;
    private LocalDateTime dateTraitement;
    private Long acquereurId;
    private Long agentId;
    private Long bienId;
    private Long creneauId;
}
