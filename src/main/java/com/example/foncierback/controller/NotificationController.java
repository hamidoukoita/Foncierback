package com.example.foncierback.controller;

import com.example.foncierback.common.response.ApiResponse;
import com.example.foncierback.dto.request.NotificationRequest;
import com.example.foncierback.dto.response.NotificationResponse;
import com.example.foncierback.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Gestion des notifications envoyées aux utilisateurs")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Créer et envoyer une nouvelle notification")
    public ResponseEntity<ApiResponse<NotificationResponse>> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Notification créée avec succès", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une notification par son ID")
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification récupérée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {
        List<NotificationResponse> list = notificationService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des notifications", list));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#utilisateurId)")
    @Operation(summary = "Lister les notifications d'un utilisateur")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUtilisateurId(@PathVariable Long utilisateurId) {
        List<NotificationResponse> list = notificationService.getByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications de l'utilisateur récupérées avec succès", list));
    }

    @PatchMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marquée comme lue", response));
    }

    @PatchMapping("/utilisateur/{utilisateurId}/tout-lire")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwnerOrAdmin(#utilisateurId)")
    @Operation(summary = "Marquer toutes les notifications d'un utilisateur comme lues")
    public ResponseEntity<ApiResponse<Integer>> toutMarquerLu(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications marquées comme lues",
                notificationService.toutMarquerLu(utilisateurId)));
    }

    @PatchMapping("/cible/{cibleType}/{cibleId}/lire")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Marquer lues ses notifications concernant un élément (ex. un projet ouvert)")
    public ResponseEntity<ApiResponse<Integer>> marquerLuesPourCible(@PathVariable String cibleType, @PathVariable Long cibleId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications marquées comme lues",
                notificationService.marquerLuesPourCible(cibleType, cibleId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification supprimée avec succès", null));
    }
}
