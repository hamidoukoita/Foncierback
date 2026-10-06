package com.example.foncierback.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvancementProjetRequest {

    @NotNull(message = "La progression est obligatoire")
    @Min(value = 0, message = "La progression doit être comprise entre 0 et 100")
    @Max(value = 100, message = "La progression doit être comprise entre 0 et 100")
    private Integer progression;

    @Size(max = 100, message = "L'étape ne doit pas dépasser 100 caractères")
    private String etapeAvancement;

    private String commentaireAvancement;
}
