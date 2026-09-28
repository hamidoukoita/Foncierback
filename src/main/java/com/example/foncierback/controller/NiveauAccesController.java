package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.NiveauAccesRequest;
import com.example.foncierback.dto.response.NiveauAccesResponse;
import com.example.foncierback.service.NiveauAccesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux-acces")
@RequiredArgsConstructor
@Tag(name = "Niveaux d'accès", description = "Gestion des niveaux d'accès de l'application")
public class NiveauAccesController {

    private final NiveauAccesService niveauAccesService;

    @PostMapping
    @Operation(summary = "Créer un nouveau niveau d'accès")
    public ResponseEntity<ApiResponse<NiveauAccesResponse>> create(@Valid @RequestBody NiveauAccesRequest request) {
        NiveauAccesResponse response = niveauAccesService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Niveau d'accès créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un niveau d'accès par son ID")
    public ResponseEntity<ApiResponse<NiveauAccesResponse>> getById(@PathVariable Long id) {
        NiveauAccesResponse response = niveauAccesService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'accès récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les niveaux d'accès")
    public ResponseEntity<ApiResponse<List<NiveauAccesResponse>>> getAll() {
        List<NiveauAccesResponse> list = niveauAccesService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des niveaux d'accès", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un niveau d'accès existant")
    public ResponseEntity<ApiResponse<NiveauAccesResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody NiveauAccesRequest request
    ) {
        NiveauAccesResponse response = niveauAccesService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'accès mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un niveau d'accès")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        niveauAccesService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'accès supprimé avec succès", null));
    }
}
