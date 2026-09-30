package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AdministrateurRequest;
import com.example.foncierback.dto.response.AdministrateurResponse;
import com.example.foncierback.service.AdministrateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrateurs")
@RequiredArgsConstructor
@Tag(name = "Administrateurs", description = "Gestion des comptes administrateurs (SuperAdmin / Admin)")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel administrateur (Admin uniquement)")
    public ResponseEntity<ApiResponse<AdministrateurResponse>> create(@Valid @RequestBody AdministrateurRequest request) {
        AdministrateurResponse response = administrateurService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Administrateur créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer un administrateur par ID")
    public ResponseEntity<ApiResponse<AdministrateurResponse>> getById(@PathVariable Long id) {
        AdministrateurResponse response = administrateurService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Administrateur récupéré avec succès", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les administrateurs")
    public ResponseEntity<ApiResponse<List<AdministrateurResponse>>> getAll() {
        List<AdministrateurResponse> list = administrateurService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des administrateurs", list));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un administrateur existant")
    public ResponseEntity<ApiResponse<AdministrateurResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AdministrateurRequest request
    ) {
        AdministrateurResponse response = administrateurService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Administrateur mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un administrateur")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        administrateurService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Administrateur supprimé avec succès", null));
    }
}
