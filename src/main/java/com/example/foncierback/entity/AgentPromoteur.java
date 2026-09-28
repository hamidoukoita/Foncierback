package com.example.foncierback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents_promoteurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentPromoteur extends Utilisateur {

    private boolean estResponsableSociete;

    private LocalDate dateAffectation;

    // --- Cardinalités / Relations ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id")
    @ToString.Exclude
    private SocietePromotrice societePromotrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_fonction_id")
    @ToString.Exclude
    private TypeFonction typeFonction;

    @OneToMany(mappedBy = "agentPromoteur")
    @ToString.Exclude
    private List<Reservation> reservationsTraitees = new ArrayList<>();

    @OneToMany(mappedBy = "agentPromoteur")
    @ToString.Exclude
    private List<RendezVous> rendezVousAssignes = new ArrayList<>();

    @OneToMany(mappedBy = "agentPromoteur")
    @ToString.Exclude
    private List<ProjetConstruction> projetsTraites = new ArrayList<>();
}
