package com.example.foncierback.dto.response;

import com.example.foncierback.dto.DescriptionMaisonDto;
import com.example.foncierback.entity.enums.StatutProposition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionProjetResponse {

    private Long id;
    private Long projetId;
    private Integer numero;
    private String titre;
    private DescriptionMaisonDto maison;
    private Long modelMaisonId;
    private String modelMaisonLibeller;
    private String modelMaisonImageUrl;
    private BigDecimal prixEstime;
    private Integer delaiMois;
    private String commentaire;
    private StatutProposition statut;
    private String reponseAcquereur;
    private LocalDateTime dateProposition;
    private LocalDateTime dateReponse;
    private String auteurNom;
    private List<FichierProjetResponse> plans;
}
