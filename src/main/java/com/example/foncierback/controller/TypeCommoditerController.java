package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.TypeCommoditerRequest;
import com.example.foncierback.dto.response.TypeCommoditerResponse;
import com.example.foncierback.service.TypeCommoditerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-commodite")
@RequiredArgsConstructor
@Tag(name = "Types de commodité", description = "Gestion des types de commodité")
public class TypeCommoditerController {

    private final TypeCommoditerService typeCommoditerService;

    @PostMapping
    @Operation(summary = "Créer un nouveau type de commodité")
    public ResponseEntity<ApiResponse<TypeCommoditerResponse>> create(@Valid @RequestBody TypeCommoditerRequest request) {
        TypeCommoditerResponse response = typeCommoditerService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Type de commodité créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un type de commodité par son ID")
    public ResponseEntity<ApiResponse<TypeCommoditerResponse>> getById(@PathVariable Long id) {
        TypeCommoditerResponse response = typeCommoditerService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de commodité récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les types de commodité")
    public ResponseEntity<ApiResponse<List<TypeCommoditerResponse>>> getAll() {
        List<TypeCommoditerResponse> list = typeCommoditerService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types de commodité", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un type de commodité existant")
    public ResponseEntity<ApiResponse<TypeCommoditerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TypeCommoditerRequest request
    ) {
        TypeCommoditerResponse response = typeCommoditerService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de commodité mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un type de commodité")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        typeCommoditerService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de commodité supprimé avec succès", null));
    }
}
