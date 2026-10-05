package com.example.foncierback.controller;

import com.example.foncierback.config.security.CustomUserDetails;
import com.example.foncierback.dto.DocumentKycResponse;
import com.example.foncierback.entity.enums.TypeDocumentKyc;
import com.example.foncierback.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/societes/kyc")
@RequiredArgsConstructor
public class KycSocieteController {

    private final KycService kycService;

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentKycResponse> uploadDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocumentKyc type) {
        
        Long societeId = userDetails.getSocieteId();
        if (societeId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        DocumentKycResponse response = kycService.uploadDocument(societeId, file, type);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/statut")
    public ResponseEntity<com.example.foncierback.dto.DossierKycStatusResponse> getStatut(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long societeId = userDetails.getSocieteId();
        if (societeId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(kycService.getStatutDossier(societeId));
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/soumettre")
    public ResponseEntity<Void> soumettreDossier(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long societeId = userDetails.getSocieteId();
        if (societeId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        kycService.soumettreDossier(societeId);
        return ResponseEntity.ok().build();
    }
}
