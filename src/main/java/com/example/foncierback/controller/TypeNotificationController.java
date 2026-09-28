package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.TypeNotificationRequest;
import com.example.foncierback.dto.response.TypeNotificationResponse;
import com.example.foncierback.service.TypeNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-notification")
@RequiredArgsConstructor
@Tag(name = "Types de Notification", description = "Gestion des catégories et types de notifications")
public class TypeNotificationController {

    private final TypeNotificationService typeNotificationService;

    @PostMapping
    @Operation(summary = "Créer un nouveau type de notification")
    public ResponseEntity<ApiResponse<TypeNotificationResponse>> create(@Valid @RequestBody TypeNotificationRequest request) {
        TypeNotificationResponse response = typeNotificationService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Type de notification créé avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un type de notification par son ID")
    public ResponseEntity<ApiResponse<TypeNotificationResponse>> getById(@PathVariable Long id) {
        TypeNotificationResponse response = typeNotificationService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de notification récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister tous les types de notification")
    public ResponseEntity<ApiResponse<List<TypeNotificationResponse>>> getAll() {
        List<TypeNotificationResponse> list = typeNotificationService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types de notification", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un type de notification existant")
    public ResponseEntity<ApiResponse<TypeNotificationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TypeNotificationRequest request
    ) {
        TypeNotificationResponse response = typeNotificationService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de notification mis à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un type de notification")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        typeNotificationService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Type de notification supprimé avec succès", null));
    }
}
