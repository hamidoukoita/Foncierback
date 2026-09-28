package com.example.foncierback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administrateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends Utilisateur {
}