package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutRendezVous;
import com.example.foncierback.entity.enums.TypeRDV;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousRequest {

    @NotNull(message = "La date du rendez-vous est obligatoire")
    private LocalDateTime dateRendezVous;

    @NotNull(message = "Le type de rendez-vous est obligatoire")
    private TypeRDV typeRDV;

    private StatutRendezVous statut;

    @Size(max = 255, message = "Le lieu ne doit pas dépasser 255 caractères")
    private String lieu;

    private String motifRefus;

    private String compteRendu;

    private Long acquereurId;

    private Long agentId;

    private Long bienId;

    private Long creneauId;
}
