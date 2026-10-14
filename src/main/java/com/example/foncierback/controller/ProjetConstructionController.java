package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AvancementProjetRequest;
import com.example.foncierback.dto.request.ProjetConstructionRequest;
import com.example.foncierback.dto.response.ProjetConstructionResponse;
import com.example.foncierback.dto.response.FichierProjetResponse;
import com.example.foncierback.entity.FichierProjet;
import com.example.foncierback.entity.enums.TypeFichierProjet;
import com.example.foncierback.service.FichierProjetService;
import com.example.foncierback.service.ProjetConstructionService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets-construction")
@RequiredArgsConstructor
@Tag(name = "Projets de Construction", description = "Demandes, choix des modèles et suivi de l'avancement des constructions")
public class ProjetConstructionController {

    private final ProjetConstructionService projetConstructionService;
    private final FichierProjetService fichierProjetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Soumettre un projet de construction à une société agréée")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> create(@Valid @RequestBody ProjetConstructionRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Projet de construction soumis avec succès", projetConstructionService.create(request)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ACQUEREUR')")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet récupéré avec succès", projetConstructionService.getById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des projets de construction", projetConstructionService.getAll()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> update(@PathVariable Long id, @Valid @RequestBody ProjetConstructionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet mis à jour avec succès", projetConstructionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        projetConstructionService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet supprimé avec succès", null));
    }

    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet accepté avec succès", projetConstructionService.valider(id)));
    }

    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> refuser(@PathVariable Long id, @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projet refusé", projetConstructionService.refuser(id, motif)));
    }

    @PatchMapping("/{id}/modele/{modelMaisonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Associer un modèle de maison compatible à un projet")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> changerModele(
            @PathVariable Long id, @PathVariable Long modelMaisonId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Modèle du projet mis à jour avec succès",
                projetConstructionService.changerModele(id, modelMaisonId)));
    }

    @PatchMapping("/{id}/avancement")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Mettre à jour l'avancement d'un projet")
    public ResponseEntity<ApiResponse<ProjetConstructionResponse>> avancement(
            @PathVariable Long id,
            @Valid @RequestBody AvancementProjetRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Avancement mis à jour avec succès", projetConstructionService.mettreAJourAvancement(id, request)));
    }

    @GetMapping("/acquereur/{acquereurId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#acquereurId)")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findByAcquereur(@PathVariable Long acquereurId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de l'acquéreur", projetConstructionService.findByAcquereur(acquereurId)));
    }

    @GetMapping("/societe/{societeId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.belongsToSocieteOrAdmin(#societeId)")
    public ResponseEntity<ApiResponse<List<ProjetConstructionResponse>>> findBySociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Projets de construction de la société", projetConstructionService.findBySociete(societeId)));
    }

    // --- Pièces jointes (Titre Foncier, plans, photos) ---

    @PostMapping(value = "/{id}/fichiers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    @Operation(summary = "Joindre une pièce au projet (PDF ou image, 15 Mo max)")
    public ResponseEntity<ApiResponse<FichierProjetResponse>> ajouterFichier(
            @PathVariable Long id,
            @RequestParam TypeFichierProjet type,
            @RequestParam("fichier") MultipartFile fichier) {
        return new ResponseEntity<>(new ApiResponse<>(true, "Pièce jointe ajoutée",
                fichierProjetService.ajouter(id, type, fichier)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/fichiers/{fichierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ACQUEREUR')")
    @Operation(summary = "Télécharger une pièce jointe du projet")
    public ResponseEntity<Resource> telechargerFichier(@PathVariable Long id, @PathVariable Long fichierId) {
        FichierProjet fichier = fichierProjetService.trouver(id, fichierId);
        Resource resource = fichierProjetService.charger(fichier);
        String type = fichier.getContentType() != null ? fichier.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(fichier.getNomOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(resource);
    }

    @DeleteMapping("/{id}/fichiers/{fichierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACQUEREUR')")
    public ResponseEntity<ApiResponse<Void>> supprimerFichier(@PathVariable Long id, @PathVariable Long fichierId) {
        fichierProjetService.supprimer(id, fichierId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Pièce jointe retirée", null));
    }
}
