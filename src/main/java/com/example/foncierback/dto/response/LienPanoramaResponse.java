package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LienPanoramaResponse {
    private Long id;
    private Long sourceId;
    private Long cibleId;
    private Double azimut;
    private Double elevation;
    private String libelle;
}
