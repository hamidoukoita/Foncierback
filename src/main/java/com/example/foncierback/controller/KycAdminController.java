package com.example.foncierback.controller;

import com.example.foncierback.dto.DocumentKycResponse;
import com.example.foncierback.dto.MotifRejetRequest;
import com.example.foncierback.service.KycService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/kyc")
@RequiredArgsConstructor
public class KycAdminController {

    private final KycService kycService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/societes/{societeId}/documents")
    public ResponseEntity<com.example.foncierback.dto.DossierKycStatusResponse> getDocumentsSociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(kycService.getStatutDossier(societeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @GetMapping("/documents/{documentId}/preview")
    public ResponseEntity<Resource> previewDocument(@PathVariable Long documentId, HttpServletRequest request) {
        Resource resource = kycService.getDocumentAsResource(documentId);
        
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // fallback
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{societeId}/valider")
    public ResponseEntity<Void> validerDossier(@PathVariable Long societeId) {
        kycService.validerDossier(societeId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{societeId}/rejeter")
    public ResponseEntity<Void> rejeterDossier(@PathVariable Long societeId, @Valid @RequestBody MotifRejetRequest request) {
        kycService.rejeterDossier(societeId, request.getMotif());
        return ResponseEntity.ok().build();
    }
}
