package com.example.foncierback.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreneauRequest {

    @NotBlank(message = "L'heure ou le libellé du créneau est obligatoire")
    @Size(max = 50, message = "Le libellé de l'heure ne doit pas dépasser 50 caractères")
    @Schema(description = "Libellé ou plage horaire du créneau", example = "09h00 - 10h00")
    private String heure;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(description = "Heure de début", example = "09:00:00")
    private LocalTime heureDebut;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(description = "Heure de fin", example = "10:00:00")
    private LocalTime heureFin;

    @Builder.Default
    @Schema(description = "Indicateur de disponibilité", example = "true")
    private Boolean disponible = true;

    @NotNull(message = "L'identifiant de la société promotrice est obligatoire")
    @Schema(description = "Identifiant de la société promotrice associée", example = "1")
    private Long societeId;
}
