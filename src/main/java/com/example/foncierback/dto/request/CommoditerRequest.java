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
public class CommoditerRequest {

    @NotBlank(message = "Le nom de la commodité est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Size(max = 100, message = "L'icône ne doit pas dépasser 100 caractères")
    private String icone;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    private Long typeCommoditeId;
}
