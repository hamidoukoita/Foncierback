package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private String numeroDossier;
    private LocalDateTime dateReservation;
    private StatutReservation statut;
    private String motifRefus;
    private LocalDateTime dateTraitement;
    private Long bienId;
    private Long acquereurId;
    private Long agentId;
}
