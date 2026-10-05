package com.example.foncierback.service.impl;

import com.example.foncierback.dto.DocumentKycResponse;
import com.example.foncierback.entity.DocumentKyc;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.entity.enums.StatutAgrement;
import com.example.foncierback.entity.enums.TypeDocumentKyc;
import com.example.foncierback.repository.DocumentKycRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.FileStorageService;
import com.example.foncierback.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final DocumentKycRepository documentKycRepository;
    private final SocietePromotriceRepository societePromotriceRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public DocumentKycResponse uploadDocument(Long societeId, MultipartFile file, TypeDocumentKyc type) {
        SocietePromotrice societe = societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new RuntimeException("Société introuvable"));

        // Vérifier si un document de ce type existe déjà pour cette société
        List<DocumentKyc> existingDocs = documentKycRepository.findBySocietePromotriceId(societeId);
        Optional<DocumentKyc> existingDocOpt = existingDocs.stream()
                .filter(d -> d.getTypeDocument() == type)
                .findFirst();

        if (existingDocOpt.isPresent()) {
            // Supprimer l'ancien fichier
            DocumentKyc existingDoc = existingDocOpt.get();
            fileStorageService.deleteFile(existingDoc.getNomFichierGenere());
            documentKycRepository.delete(existingDoc);
        }

        // Sauvegarder le nouveau fichier
        String fileName = fileStorageService.storeFile(file);

        DocumentKyc documentKyc = DocumentKyc.builder()
                .nomOriginal(file.getOriginalFilename())
                .nomFichierGenere(fileName)
                .typeDocument(type)
                .typeMime(file.getContentType())
                .tailleFichier(file.getSize())
                .cheminAcces(fileName)
                .dateAjout(LocalDateTime.now())
                .societePromotrice(societe)
                .build();

        documentKyc = documentKycRepository.save(documentKyc);

        return mapToResponse(documentKyc);
    }

    @Override
    public com.example.foncierback.dto.DossierKycStatusResponse getStatutDossier(Long societeId) {
        SocietePromotrice societe = societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new RuntimeException("Société introuvable"));

        List<DocumentKyc> documents = documentKycRepository.findBySocietePromotriceId(societeId);
        List<DocumentKycResponse> docsResponse = documents.stream().map(this::mapToResponse).collect(Collectors.toList());

        return com.example.foncierback.dto.DossierKycStatusResponse.builder()
                .statutAgrement(societe.getStatutAgrement())
                .documents(docsResponse)
                .build();
    }

    @Override
    public Resource getDocumentAsResource(Long documentId) {
        DocumentKyc document = documentKycRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        return fileStorageService.loadFileAsResource(document.getNomFichierGenere());
    }

    @Override
    @Transactional
    public void soumettreDossier(Long societeId) {
        SocietePromotrice societe = societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new RuntimeException("Société introuvable"));

        List<DocumentKyc> documents = documentKycRepository.findBySocietePromotriceId(societeId);

        boolean hasNif = documents.stream().anyMatch(d -> d.getTypeDocument() == TypeDocumentKyc.NIF);
        boolean hasRccm = documents.stream().anyMatch(d -> d.getTypeDocument() == TypeDocumentKyc.RCCM);
        boolean hasAgrement = documents.stream().anyMatch(d -> d.getTypeDocument() == TypeDocumentKyc.AGREMENT_MINISTERIEL);

        if (!hasNif || !hasRccm || !hasAgrement) {
            throw new IllegalStateException("Le dossier KYC est incomplet. Veuillez fournir les 3 documents obligatoires (NIF, RCCM, Agrément).");
        }

        societe.setStatutAgrement(StatutAgrement.EN_ATTENTE);
        societePromotriceRepository.save(societe);
    }

    @Override
    @Transactional
    public void validerDossier(Long societeId) {
        SocietePromotrice societe = societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new RuntimeException("Société introuvable"));
        
        societe.setStatutAgrement(StatutAgrement.VERIFIER);
        societePromotriceRepository.save(societe);
    }

    @Override
    @Transactional
    public void rejeterDossier(Long societeId, String motif) {
        SocietePromotrice societe = societePromotriceRepository.findById(societeId)
                .orElseThrow(() -> new RuntimeException("Société introuvable"));
        
        societe.setStatutAgrement(StatutAgrement.REFUSER);
        // Note: L'entité SocietePromotrice n'a pas de champ motifRejet actuellement, 
        // nous le laissons ainsi pour l'instant ou on l'ajoutera si nécessaire.
        societePromotriceRepository.save(societe);
    }

    private DocumentKycResponse mapToResponse(DocumentKyc documentKyc) {
        return DocumentKycResponse.builder()
                .id(documentKyc.getId())
                .nomOriginal(documentKyc.getNomOriginal())
                .typeDocument(documentKyc.getTypeDocument())
                .tailleFichier(documentKyc.getTailleFichier())
                .dateAjout(documentKyc.getDateAjout())
                .urlPreview("/api/admin/kyc/documents/" + documentKyc.getId() + "/preview")
                .build();
    }
}
