package com.example.foncierback.service;

import com.example.foncierback.dto.request.MediaLienRequest;
import com.example.foncierback.dto.request.MediaUpdateRequest;
import com.example.foncierback.dto.response.MediaFoncierResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaFoncierService {

    List<MediaFoncierResponse> getByProgramme(Long programmeId);

    List<MediaFoncierResponse> getByBien(Long bienId);

    MediaFoncierResponse ajouterFichierProgramme(Long programmeId, MultipartFile fichier, String type, String titre, String description);

    MediaFoncierResponse ajouterFichierBien(Long bienId, MultipartFile fichier, String type, String titre, String description);

    MediaFoncierResponse ajouterLienProgramme(Long programmeId, MediaLienRequest request);

    MediaFoncierResponse ajouterLienBien(Long bienId, MediaLienRequest request);

    MediaFoncierResponse update(Long id, MediaUpdateRequest request);

    void delete(Long id);
}
