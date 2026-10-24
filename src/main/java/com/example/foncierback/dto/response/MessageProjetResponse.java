package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.CoteEchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageProjetResponse {

    private Long id;
    private String texte;
    private CoteEchange cote;
    private String auteurNom;
    private LocalDateTime dateEnvoi;
}
