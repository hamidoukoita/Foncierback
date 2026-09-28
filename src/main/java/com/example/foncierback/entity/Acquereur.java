package com.example.foncierback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "acquereurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Acquereur extends Utilisateur {

    private String paysResidence;

    private String preference;
}
