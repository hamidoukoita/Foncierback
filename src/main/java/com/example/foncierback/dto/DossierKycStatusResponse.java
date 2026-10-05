package com.example.foncierback.dto;

import com.example.foncierback.entity.enums.StatutAgrement;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DossierKycStatusResponse {
    private StatutAgrement statutAgrement;
    private List<DocumentKycResponse> documents;
}
