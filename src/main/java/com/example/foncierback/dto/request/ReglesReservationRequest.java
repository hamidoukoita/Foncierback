package com.example.foncierback.dto.request;

import com.example.foncierback.service.ReglesReservationResolver;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglesReservationRequest {

    @NotNull(message = "Le délai d'attribution est obligatoire")
    @Min(value = ReglesReservationResolver.DELAI_MIN_HEURES, message = "Le délai d'attribution doit être d'au moins 24 heures")
    @Max(value = ReglesReservationResolver.DELAI_MAX_HEURES, message = "Le délai d'attribution ne peut pas dépasser 14 jours")
    private Integer delaiAttributionHeures;

    @NotNull(message = "Indiquez si un bien peut être réservé plusieurs fois")
    private Boolean reservationMultiple;
}
