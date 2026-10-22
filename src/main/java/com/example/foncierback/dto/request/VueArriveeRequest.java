package com.example.foncierback.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Direction regardée en entrant dans la pièce cible d'un point ; les deux à null la réinitialisent. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VueArriveeRequest {

    @DecimalMin(value = "-180", message = "L'azimut doit être compris entre -180° et 180°")
    @DecimalMax(value = "180", message = "L'azimut doit être compris entre -180° et 180°")
    private Double azimut;

    @DecimalMin(value = "-90", message = "L'élévation doit être comprise entre -90° et 90°")
    @DecimalMax(value = "90", message = "L'élévation doit être comprise entre -90° et 90°")
    private Double elevation;
}
