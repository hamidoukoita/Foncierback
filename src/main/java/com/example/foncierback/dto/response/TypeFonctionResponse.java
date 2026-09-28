package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeFonctionResponse {

    private Long id;
    private String libelle;
    private String code;
    private String description;
    private Long niveauAccesId;
    private String niveauAccesLibelle;
}
