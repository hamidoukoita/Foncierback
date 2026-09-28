package com.example.foncierback.dto.request;

import com.example.foncierback.entity.enums.StatutReservation;
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
public class ReservationRequest {

    @Size(max = 100, message = "Le numéro de dossier ne doit pas dépasser 100 caractères")
    private String numeroDossier;

    private LocalDateTime dateReservation;

    private StatutReservation statut;

    private String motifRefus;

    @NotNull(message = "L'identifiant du bien foncier est obligatoire")
    private Long bienId;

    private Long acquereurId;

    private Long agentId;
}
