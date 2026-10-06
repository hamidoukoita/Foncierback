package com.example.foncierback.dto;

import com.example.foncierback.entity.enums.TypeDocumentKyc;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentKycResponse {
    private Long id;
    private String nomOriginal;
    private TypeDocumentKyc typeDocument;
    private Long tailleFichier;
    private LocalDateTime dateAjout;
    private String urlPreview;
}
