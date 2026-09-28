package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.response.UtilisateurResponse;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion transversale des utilisateurs et de leurs statuts")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son ID")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getById(@PathVariable Long id) {
        UtilisateurResponse response = utilisateurService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<ApiResponse<List<UtilisateurResponse>>> getAll() {
        List<UtilisateurResponse> list = utilisateurService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des utilisateurs", list));
    }

    @GetMapping("/telephone/{telephone}")
    @Operation(summary = "Rechercher un utilisateur par son numéro de téléphone")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getByTelephone(@PathVariable String telephone) {
        UtilisateurResponse response = utilisateurService.getByTelephone(telephone);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur trouvé avec succès", response));
    }

    @PatchMapping("/{id}/toggle-statut")
    @Operation(summary = "Basculer le statut d'un utilisateur (ACTIF <-> SUSPENDU)")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> toggleStatut(@PathVariable Long id) {
        UtilisateurResponse response = utilisateurService.toggleStatut(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut de l'utilisateur modifié avec succès", response));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> changeStatut(
            @PathVariable Long id,
            @RequestParam StatutCompte statut
    ) {
        UtilisateurResponse response = utilisateurService.changeStatut(id, statut);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut de l'utilisateur mis à jour avec succès", response));
    }
}
