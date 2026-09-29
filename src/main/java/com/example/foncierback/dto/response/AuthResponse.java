package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String role;
    private StatutCompte statut;
}