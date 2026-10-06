package com.example.foncierback.service;

import com.example.foncierback.dto.DocumentKycResponse;
import com.example.foncierback.entity.enums.TypeDocumentKyc;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KycService {
    DocumentKycResponse uploadDocument(Long societeId, MultipartFile file, TypeDocumentKyc type);
    com.example.foncierback.dto.DossierKycStatusResponse getStatutDossier(Long societeId);
    Resource getDocumentAsResource(Long documentId);
    void soumettreDossier(Long societeId);
    void validerDossier(Long societeId);
    void rejeterDossier(Long societeId, String motif);
}
