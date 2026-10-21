package com.example.foncierback.service;

import com.example.foncierback.dto.request.MediaUpdateRequest;
import com.example.foncierback.dto.response.MediaFoncierResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaFoncierService {

    List<MediaFoncierResponse> getByProgramme(Long programmeId);

    List<MediaFoncierResponse> getByBien(Long bienId);

    List<MediaFoncierResponse> getByModele(Long modeleId);

    MediaFoncierResponse ajouterFichierProgramme(Long programmeId, MultipartFile fichier, String type, String titre, String description);

    MediaFoncierResponse ajouterFichierBien(Long bienId, MultipartFile fichier, String type, String titre, String description);

    MediaFoncierResponse ajouterFichierModele(Long modeleId, MultipartFile fichier, String type, String titre, String description);

    MediaFoncierResponse update(Long id, MediaUpdateRequest request);

    void delete(Long id);

    /** Place la photo en tête de sa galerie : elle devient la couverture de la cité, du bien ou du modèle. */
    MediaFoncierResponse definirCouverture(Long id);
}
