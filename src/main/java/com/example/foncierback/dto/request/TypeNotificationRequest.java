package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeNotificationRequest {

    @NotBlank(message = "Le libellé du type de notification est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libelle;

    @Size(max = 50, message = "Le code ne doit pas dépasser 50 caractères")
    private String code;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;
}
