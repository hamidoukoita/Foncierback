package com.example.foncierback.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProximiteCommoditeRequest {

    @NotNull(message = "La commodité est obligatoire")
    private Long commoditeId;

    /** Cité ciblée (exclusif avec bienId). */
    private Long programmeId;

    /** Parcelle individuelle ciblée (exclusif avec programmeId). */
    private Long bienId;

    @DecimalMin(value = "0.0", message = "La distance ne peut pas être négative")
    private Double distanceKm;
}
