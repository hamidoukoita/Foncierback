package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.response.UtilisateurResponse;
import com.example.foncierback.entity.enums.StatutCompte;
import com.example.foncierback.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion globale des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#id)")
    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getById(@PathVariable Long id) {
        UtilisateurResponse response = utilisateurService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les utilisateurs (Admin uniquement)")
    public ResponseEntity<ApiResponse<List<UtilisateurResponse>>> getAll() {
        List<UtilisateurResponse> list = utilisateurService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des utilisateurs récupérée", list));
    }

    @GetMapping("/by-telephone")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechercher un utilisateur par son numéro de téléphone")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getByTelephone(@RequestParam String telephone) {
        UtilisateurResponse response = utilisateurService.getByTelephone(telephone);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur trouvé", response));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier le statut d'un compte utilisateur (Admin uniquement)")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> changeStatut(
            @PathVariable Long id,
            @RequestParam StatutCompte statut
    ) {
        UtilisateurResponse response = utilisateurService.changeStatut(id, statut);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut de l'utilisateur modifié avec succès", response));
    }

    @PatchMapping("/{id}/toggle-statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Basculer le statut d'un compte (ACTIF <-> SUSPENDU) (Admin uniquement)")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> toggleStatut(@PathVariable Long id) {
        UtilisateurResponse response = utilisateurService.toggleStatut(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Statut basculé avec succès", response));
    }
}
