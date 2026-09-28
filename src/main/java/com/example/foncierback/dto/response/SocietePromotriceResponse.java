package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutAgrement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocietePromotriceResponse {

    private Long id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private String nif;
    private String numeroAgrement;
    private LocalDate dateAgrement;
    private StatutAgrement statutAgrement;
    private String logoUrl;
    private String siteWeb;
    private String description;
}
