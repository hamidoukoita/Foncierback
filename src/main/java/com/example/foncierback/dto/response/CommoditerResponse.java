package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommoditerResponse {

    private Long id;
    private String nom;
    private String icone;
    private String description;
    private Long typeCommoditeId;
    private String typeCommoditeLibelle;
}
