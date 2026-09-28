package com.example.foncierback.service;

import com.example.foncierback.dto.response.UtilisateurResponse;
import com.example.foncierback.entity.Utilisateur;
import com.example.foncierback.entity.enums.StatutCompte;

import java.util.List;

public interface UtilisateurService {

    UtilisateurResponse getById(Long id);

    List<UtilisateurResponse> getAll();

    UtilisateurResponse toggleStatut(Long id);

    UtilisateurResponse changeStatut(Long id, StatutCompte statut);

    UtilisateurResponse getByTelephone(String telephone);

    Utilisateur findEntityById(Long id);
}
