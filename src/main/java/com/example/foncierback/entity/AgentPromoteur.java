package com.example.foncierback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "agents_promoteurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentPromoteur extends Utilisateur {

    private boolean estResponsableSociete;

    private LocalDate dateAffectation;
}