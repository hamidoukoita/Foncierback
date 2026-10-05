package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.MediaLienRequest;
import com.example.foncierback.dto.request.MediaUpdateRequest;
import com.example.foncierback.dto.response.MediaFoncierResponse;
import com.example.foncierback.service.MediaFoncierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medias")
@RequiredArgsConstructor
@Tag(name = "Médias fonciers", description = "Photos, panoramas 360° et visites virtuelles des programmes et des biens")
public class MediaFoncierController {

    private final MediaFoncierService mediaService;

    @GetMapping("/programme/{programmeId}")
    @Operation(summary = "Lister les médias d'un programme")
    public ResponseEntity<ApiResponse<List<MediaFoncierResponse>>> getByProgramme(@PathVariable Long programmeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Médias du programme", mediaService.getByProgramme(programmeId)));
    }

    @GetMapping("/bien/{bienId}")
    @Operation(summary = "Lister les médias d'un bien (lot ou parcelle)")
    public ResponseEntity<ApiResponse<List<MediaFoncierResponse>>> getByBien(@PathVariable Long bienId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Médias du bien", mediaService.getByBien(bienId)));
    }

    @PostMapping(value = "/programme/{programmeId}/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter une photo ou un panorama 360° à un programme")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> ajouterFichierProgramme(
            @PathVariable Long programmeId,
            @RequestParam("file") MultipartFile fichier,
            @RequestParam("type") String type,
            @RequestParam(value = "titre", required = false) String titre,
            @RequestParam(value = "description", required = false) String description) {
        MediaFoncierResponse response = mediaService.ajouterFichierProgramme(programmeId, fichier, type, titre, description);
        return new ResponseEntity<>(new ApiResponse<>(true, "Média ajouté avec succès", response), HttpStatus.CREATED);
    }

    @PostMapping(value = "/bien/{bienId}/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter une photo ou un panorama 360° à un bien")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> ajouterFichierBien(
            @PathVariable Long bienId,
            @RequestParam("file") MultipartFile fichier,
            @RequestParam("type") String type,
            @RequestParam(value = "titre", required = false) String titre,
            @RequestParam(value = "description", required = false) String description) {
        MediaFoncierResponse response = mediaService.ajouterFichierBien(bienId, fichier, type, titre, description);
        return new ResponseEntity<>(new ApiResponse<>(true, "Média ajouté avec succès", response), HttpStatus.CREATED);
    }

    @PostMapping("/programme/{programmeId}/lien")
    @Operation(summary = "Ajouter une visite virtuelle (lien https) à un programme")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> ajouterLienProgramme(
            @PathVariable Long programmeId, @Valid @RequestBody MediaLienRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Visite virtuelle ajoutée avec succès",
                mediaService.ajouterLienProgramme(programmeId, request)), HttpStatus.CREATED);
    }

    @PostMapping("/bien/{bienId}/lien")
    @Operation(summary = "Ajouter une visite virtuelle (lien https) à un bien")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> ajouterLienBien(
            @PathVariable Long bienId, @Valid @RequestBody MediaLienRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Visite virtuelle ajoutée avec succès",
                mediaService.ajouterLienBien(bienId, request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier le titre et la description d'un média")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MediaUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Média mis à jour avec succès", mediaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un média")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Média supprimé avec succès", null));
    }
}
