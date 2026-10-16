package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutParcelle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaisonCleEnMainResponse {
    private Long id;
    private String reference;
    private String titre;
    private String description;
    private BigDecimal prix;
    private Double superficie;
    private Double surfaceHabitable;
    private Integer nombreChambres;
    private Integer nombreSallesBain;
    private Integer nombreNiveaux;
    private String numeroTitreFoncier;
    private String adresse;
    private Double latitude;
    private Double longitude;
    private StatutParcelle statut;
    private Boolean reservationMultiple;
    /** Photo de couverture (chemin relatif /uploads/medias/…), null si aucune photo. */
    private String imageUrl;
    private Long nombrePanoramas;
    private Long societeId;
    private String societeNom;
    /** Logo de la société promotrice, affiché sur la publication côté acquéreur. */
    private String societeLogoUrl;

    @Builder.Default
    private List<String> equipements = new ArrayList<>();

    @Builder.Default
    private List<CommoditerResponse> commodites = new ArrayList<>();
}
