package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProximiteCommoditeResponse {

    private Long id;
    private Double distanceKm;
    private Long programmeId;
    private String programmeNom;
    private Long bienId;
    private String bienReference;
    private CommoditerResponse commodite;
}
