package com.example.foncierback.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreneauResponse {

    private Long id;
    private String heure;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime heureDebut;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime heureFin;

    private Boolean disponible;
    private Long societeId;
    private String societeNom;
}
