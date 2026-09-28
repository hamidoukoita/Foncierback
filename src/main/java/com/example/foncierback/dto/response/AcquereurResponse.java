package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquereurResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private StatutCompte statut;
    private LocalDateTime dateCreation;
    private String paysResidence;
    private String preference;
}
