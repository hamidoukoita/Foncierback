package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.response.AcquereurResponse;
import com.example.foncierback.service.AcquereurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acquereurs")
@RequiredArgsConstructor
@Tag(name = "Acquéreurs", description = "Gestion des profils acquéreurs")
public class AcquereurController {

    private final AcquereurService acquereurService;

    @PostMapping
    @Operation(summary = "Créer un nouvel acquéreur")
    public ResponseEntity<ApiResponse<AcquereurResponse>> create(@Valid @RequestBody AcquereurRequest request) {
        AcquereurResponse response = acquereurService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Acquéreur créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un acquéreur par son ID")
    public ResponseEntity<ApiResponse<AcquereurResponse>> getById(@PathVariable Long id) {
        AcquereurResponse response = acquereurService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Acquéreur récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les acquéreurs")
    public ResponseEntity<ApiResponse<List<AcquereurResponse>>> getAll() {
        List<AcquereurResponse> list = acquereurService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des acquéreurs", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les informations d'un acquéreur")
    public ResponseEntity<ApiResponse<AcquereurResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AcquereurRequest request
    ) {
        AcquereurResponse response = acquereurService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Acquéreur mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un acquéreur")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        acquereurService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Acquéreur supprimé avec succès", null));
    }
}
