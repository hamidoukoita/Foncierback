package com.example.foncierback.service;

import com.example.foncierback.dto.response.FichierProjetResponse;
import com.example.foncierback.entity.FichierProjet;
import com.example.foncierback.entity.enums.TypeFichierProjet;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/** Pièces jointes d'un projet de construction (Titre Foncier, plans, photos). */
public interface FichierProjetService {

    FichierProjetResponse ajouter(Long projetId, TypeFichierProjet type, MultipartFile fichier);

    FichierProjet trouver(Long projetId, Long fichierId);

    Resource charger(FichierProjet fichier);

    void supprimer(Long projetId, Long fichierId);
}
