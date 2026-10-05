package com.example.foncierback.dto.response;

import com.example.foncierback.entity.enums.TypeMedia;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFoncierResponse {
    private Long id;
    private TypeMedia type;
    private String titre;
    private String description;
    /** Chemin relatif (/uploads/medias/...) pour un fichier, lien https pour une visite virtuelle. */
    private String url;
    private String typeContenu;
    private Integer ordre;
    private LocalDateTime dateAjout;
    private Long programmeId;
    private Long bienId;
}
