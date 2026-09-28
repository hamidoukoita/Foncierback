package com.example.foncierback.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotBlank(message = "Le titre de la notification est obligatoire")
    private String titre;

    @NotBlank(message = "Le message de la notification est obligatoire")
    private String message;

    private Long typeNotificationId;

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private Long utilisateurId;
}
