package com.example.foncierback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acquereurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Acquereur extends Utilisateur {

    private String paysResidence;

    private String preference;

    // --- Cardinalités / Relations miroirs ---

    @OneToMany(mappedBy = "acquereur")
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "acquereur")
    @ToString.Exclude
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "acquereur")
    @ToString.Exclude
    private List<ProjetConstruction> projetsConstruction = new ArrayList<>();
}
