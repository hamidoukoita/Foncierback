package com.example.foncierback.dto.request;

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

    private Long programmeId;

    private Long bienId;

    private Double distanceKm;
}
