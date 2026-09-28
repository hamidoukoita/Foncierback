package com.example.foncierback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String titre;
    private String message;
    private Boolean lue;
    private LocalDateTime dateEnvoi;
    private Long typeNotificationId;
    private String typeNotificationLibelle;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private String utilisateurTelephone;
}
