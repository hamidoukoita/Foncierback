package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.TypeFichierProjet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichierProjetResponse {
    private Long id;
    private TypeFichierProjet type;
    private String nomOriginal;
    private String contentType;
    private Long taille;
    private LocalDateTime dateAjout;
}
