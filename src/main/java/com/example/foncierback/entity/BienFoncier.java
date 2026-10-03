package com.example.foncierback.entity;

import com.example.foncierback.entity.enums.StatutParcelle;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "biens_fonciers")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class BienFoncier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @NotNull(message = "La superficie est obligatoire")
    @Positive(message = "La superficie doit être strictement positive")
    @Column(nullable = false)
    private Double superficie;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être strictement positif")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prix;

    @Positive(message = "La façade doit être strictement positive")
    @Column(name = "facade")
    private Double facade;

    @Positive(message = "La profondeur doit être strictement positive")
    @Column(name = "profondeur")
    private Double profondeur;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /**
     * Géométrie vectorielle du bien dans le plan de masse.
     * Format JSON : {"type":"POLYGON","points":[{"x":10,"y":20}, ...]}
     * Le champ reste nullable afin de conserver les biens existants sans géométrie.
     */
    @Column(name = "geometry_json", columnDefinition = "LONGTEXT")
    private String geometryJson;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutParcelle statut;

    @Builder.Default
    @OneToMany(mappedBy = "bienFoncier", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "biens_commodites",
        joinColumns = @JoinColumn(name = "bien_foncier_id"),
        inverseJoinColumns = @JoinColumn(name = "commodite_id")
    )
    @ToString.Exclude
    private List<Commoditer> commodites = new ArrayList<>();
}
