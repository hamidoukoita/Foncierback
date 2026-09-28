package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.TypeFonctionRequest;
import com.example.foncierback.dto.response.TypeFonctionResponse;
import com.example.foncierback.service.TypeFonctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-fonction")
@RequiredArgsConstructor
@Tag(name = "Types de fonction", description = "Gestion des types de fonction")
public class TypeFonctionController {

    private final TypeFonctionService typeFonctionService;

    @PostMapping
    @Operation(summary = "Créer un nouveau type de fonction")
    public ResponseEntity<ApiResponse<TypeFonctionResponse>> create(@Valid @RequestBody TypeFonctionRequest request) {
        TypeFonctionResponse response = typeFonctionService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Type de fonction créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un type de fonction par son ID")
    public ResponseEntity<ApiResponse<TypeFonctionResponse>> getById(@PathVariable Long id) {
        TypeFonctionResponse response = typeFonctionService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de fonction récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les types de fonction")
    public ResponseEntity<ApiResponse<List<TypeFonctionResponse>>> getAll() {
        List<TypeFonctionResponse> list = typeFonctionService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types de fonction", list));
    }

    @GetMapping("/niveau-acces/{niveauAccesId}")
    @Operation(summary = "Lister les types de fonction associés à un niveau d'accès")
    public ResponseEntity<ApiResponse<List<TypeFonctionResponse>>> getByNiveauAccesId(@PathVariable Long niveauAccesId) {
        List<TypeFonctionResponse> list = typeFonctionService.getByNiveauAccesId(niveauAccesId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types de fonction pour le niveau d'accès " + niveauAccesId, list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un type de fonction existant")
    public ResponseEntity<ApiResponse<TypeFonctionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TypeFonctionRequest request
    ) {
        TypeFonctionResponse response = typeFonctionService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de fonction mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un type de fonction")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        typeFonctionService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de fonction supprimé avec succès", null));
    }
}
