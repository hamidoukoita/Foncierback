package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.LienPanoramaRequest;
import com.example.foncierback.dto.request.MediaUpdateRequest;
import com.example.foncierback.dto.request.VueArriveeRequest;
import com.example.foncierback.dto.response.LienPanoramaResponse;
import com.example.foncierback.dto.response.MediaFoncierResponse;
import com.example.foncierback.dto.response.VisiteVirtuelleResponse;
import com.example.foncierback.service.LienPanoramaService;
import com.example.foncierback.service.MediaFoncierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medias")
@RequiredArgsConstructor
@Tag(name = "Médias fonciers", description = "Photos, panoramas 360° et visites virtuelles des programmes et des biens")
public class MediaFoncierController {

    private final MediaFoncierService mediaService;
    private final LienPanoramaService lienService;

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

    @GetMapping("/modele/{modeleId}")
    @Operation(summary = "Lister les médias d'un modèle de maison du catalogue")
    public ResponseEntity<ApiResponse<List<MediaFoncierResponse>>> getByModele(@PathVariable Long modeleId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Médias du modèle", mediaService.getByModele(modeleId)));
    }

    @PostMapping(value = "/programme/{programmeId}/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
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
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
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

    @PostMapping(value = "/modele/{modeleId}/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Ajouter une photo ou un panorama 360° à un modèle de maison")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> ajouterFichierModele(
            @PathVariable Long modeleId,
            @RequestParam("file") MultipartFile fichier,
            @RequestParam("type") String type,
            @RequestParam(value = "titre", required = false) String titre,
            @RequestParam(value = "description", required = false) String description) {
        MediaFoncierResponse response = mediaService.ajouterFichierModele(modeleId, fichier, type, titre, description);
        return new ResponseEntity<>(new ApiResponse<>(true, "Média ajouté avec succès", response), HttpStatus.CREATED);
    }

    // ------------------------------------------------------------ Visite virtuelle (maisons clés en main)

    @GetMapping("/bien/{bienId}/visite")
    @Operation(summary = "Visite virtuelle d'une maison : pièce de départ et points de passage")
    public ResponseEntity<ApiResponse<VisiteVirtuelleResponse>> getVisite(@PathVariable Long bienId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Visite virtuelle de la maison", lienService.getVisite(bienId)));
    }

    @PutMapping("/bien/{bienId}/visite/depart/{panoramaId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Choisir la pièce par laquelle commence la visite")
    public ResponseEntity<ApiResponse<VisiteVirtuelleResponse>> definirDepart(
            @PathVariable Long bienId, @PathVariable Long panoramaId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Pièce de départ enregistrée",
                lienService.definirDepart(bienId, panoramaId)));
    }

    @PutMapping("/liens/{id}/vue-arrivee")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Régler la direction regardée en arrivant par ce point (null : automatique)")
    public ResponseEntity<ApiResponse<LienPanoramaResponse>> definirVueArrivee(
            @PathVariable Long id, @Valid @RequestBody VueArriveeRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Vue d'arrivée enregistrée", lienService.definirVueArrivee(id, request)));
    }

    @PostMapping("/liens")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Relier deux panoramas d'une maison clé en main (et, au choix, créer le retour)")
    public ResponseEntity<ApiResponse<List<LienPanoramaResponse>>> creerLien(@Valid @RequestBody LienPanoramaRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Point de passage ajouté", lienService.creer(request)), HttpStatus.CREATED);
    }

    @PutMapping("/liens/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Déplacer ou renommer un point de passage")
    public ResponseEntity<ApiResponse<LienPanoramaResponse>> modifierLien(
            @PathVariable Long id, @Valid @RequestBody LienPanoramaRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Point de passage mis à jour", lienService.modifier(id, request)));
    }

    @DeleteMapping("/liens/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Supprimer un point de passage")
    public ResponseEntity<ApiResponse<Void>> supprimerLien(@PathVariable Long id) {
        lienService.supprimer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Point de passage supprimé", null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Modifier le titre et la description d'un média")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MediaUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Média mis à jour avec succès", mediaService.update(id, request)));
    }

    @PatchMapping("/{id}/couverture")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Définir une photo comme couverture de la cité ou du bien")
    public ResponseEntity<ApiResponse<MediaFoncierResponse>> definirCouverture(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Photo de couverture mise à jour", mediaService.definirCouverture(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AGENT') and hasAuthority('RESPONSABLE_SOCIETE'))")
    @Operation(summary = "Supprimer un média")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Média supprimé avec succès", null));
    }
}
