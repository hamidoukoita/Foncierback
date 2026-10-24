package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.EquipementMaison;
import com.example.foncierback.entity.enums.StyleMaison;
import com.example.foncierback.entity.enums.TypeCuisine;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.EnumSet;
import java.util.Set;

/**
 * Description d'une maison à construire : souhaits de l'acquéreur (projet personnalisé)
 * ou maison proposée par la société. Les enums sont stockés en VARCHAR pour pouvoir évoluer.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescriptionMaison {

    /** 1 = plain-pied, 2 = R+1, 3 = R+2. */
    @Column(name = "nombre_niveaux")
    private Integer nombreNiveaux;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "style_maison", length = 30)
    private StyleMaison style;

    @Column(name = "nombre_chambres")
    private Integer nombreChambres;

    /** Chambres avec salle de bain privative, comprises dans le nombre de chambres. */
    @Column(name = "nombre_suites")
    private Integer nombreSuites;

    @Column(name = "nombre_salles_bain")
    private Integer nombreSallesBain;

    @Column(name = "nombre_salons")
    private Integer nombreSalons;

    @Column(name = "salle_a_manger")
    private Boolean salleAManger;

    @Column(name = "toilettes_visiteurs")
    private Boolean toilettesVisiteurs;

    @Column(name = "magasin")
    private Boolean magasin;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "type_cuisine", length = 30)
    private TypeCuisine cuisine;

    /** 0 : pas de garage. */
    @Column(name = "places_garage")
    private Integer placesGarage;

    @Convert(converter = EquipementsMaisonConverter.class)
    @Column(name = "equipements", length = 300)
    @Builder.Default
    private Set<EquipementMaison> equipements = EnumSet.noneOf(EquipementMaison.class);

    /** Renseignée par la société dans ses propositions ; l'acquéreur ne la donne pas. */
    @Column(name = "surface_habitable")
    private Double surfaceHabitable;

    @Column(name = "precisions", columnDefinition = "TEXT")
    private String precisions;
}
