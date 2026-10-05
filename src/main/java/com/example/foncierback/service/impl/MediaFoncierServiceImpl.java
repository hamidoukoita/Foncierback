package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.MediaLienRequest;
import com.example.foncierback.dto.request.MediaUpdateRequest;
import com.example.foncierback.dto.response.MediaFoncierResponse;
import com.example.foncierback.entity.BienFoncier;
import com.example.foncierback.entity.MediaFoncier;
import com.example.foncierback.entity.ProgrammeFoncier;
import com.example.foncierback.entity.enums.TypeMedia;
import com.example.foncierback.repository.BienFoncierRepository;
import com.example.foncierback.repository.MediaFoncierRepository;
import com.example.foncierback.repository.ProgrammeFoncierRepository;
import com.example.foncierback.service.MediaFoncierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaFoncierServiceImpl implements MediaFoncierService {

    private static final long TAILLE_MAX_OCTETS = 25L * 1024 * 1024;
    /** Types acceptés : l'extension est déduite du type de contenu, jamais du nom envoyé par le client. */
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );
    private static final String PREFIXE_URL = "/uploads/medias/";

    private final MediaFoncierRepository mediaRepository;
    private final ProgrammeFoncierRepository programmeRepository;
    private final BienFoncierRepository bienRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // ------------------------------------------------------------------ Lecture

    @Override
    @Transactional(readOnly = true)
    public List<MediaFoncierResponse> getByProgramme(Long programmeId) {
        return mediaRepository.findByProgrammeFoncierIdOrderByOrdreAscIdAsc(programmeId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaFoncierResponse> getByBien(Long bienId) {
        return mediaRepository.findByBienFoncierIdOrderByOrdreAscIdAsc(bienId)
                .stream().map(this::mapToResponse).toList();
    }

    // ------------------------------------------------------------------ Fichiers (photo / panorama)

    @Override
    public MediaFoncierResponse ajouterFichierProgramme(Long programmeId, MultipartFile fichier, String type, String titre, String description) {
        ProgrammeFoncier programme = findProgramme(programmeId);
        MediaFoncier media = construireDepuisFichier(fichier, type, titre, description);
        media.setProgrammeFoncier(programme);
        media.setOrdre(mediaRepository.maxOrdreProgramme(programmeId) + 1);
        return mapToResponse(mediaRepository.save(media));
    }

    @Override
    public MediaFoncierResponse ajouterFichierBien(Long bienId, MultipartFile fichier, String type, String titre, String description) {
        BienFoncier bien = findBien(bienId);
        MediaFoncier media = construireDepuisFichier(fichier, type, titre, description);
        media.setBienFoncier(bien);
        media.setOrdre(mediaRepository.maxOrdreBien(bienId) + 1);
        return mapToResponse(mediaRepository.save(media));
    }

    // ------------------------------------------------------------------ Liens (visite virtuelle)

    @Override
    public MediaFoncierResponse ajouterLienProgramme(Long programmeId, MediaLienRequest request) {
        ProgrammeFoncier programme = findProgramme(programmeId);
        MediaFoncier media = construireDepuisLien(request);
        media.setProgrammeFoncier(programme);
        media.setOrdre(mediaRepository.maxOrdreProgramme(programmeId) + 1);
        return mapToResponse(mediaRepository.save(media));
    }

    @Override
    public MediaFoncierResponse ajouterLienBien(Long bienId, MediaLienRequest request) {
        BienFoncier bien = findBien(bienId);
        MediaFoncier media = construireDepuisLien(request);
        media.setBienFoncier(bien);
        media.setOrdre(mediaRepository.maxOrdreBien(bienId) + 1);
        return mapToResponse(mediaRepository.save(media));
    }

    // ------------------------------------------------------------------ Mise à jour / suppression

    @Override
    public MediaFoncierResponse update(Long id, MediaUpdateRequest request) {
        MediaFoncier media = findMedia(id);
        media.setTitre(nettoyer(request.getTitre()));
        media.setDescription(nettoyer(request.getDescription()));
        return mapToResponse(mediaRepository.save(media));
    }

    @Override
    public void delete(Long id) {
        MediaFoncier media = findMedia(id);
        String nomFichier = media.getNomFichier();
        mediaRepository.delete(media);
        if (nomFichier != null) {
            try {
                Files.deleteIfExists(dossierMedias().resolve(nomFichier));
            } catch (IOException e) {
                // La ligne en base est supprimée ; un fichier orphelin ne doit pas faire échouer la requête.
                log.warn("Impossible de supprimer le fichier média {} : {}", nomFichier, e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ Outils internes

    private MediaFoncier construireDepuisFichier(MultipartFile fichier, String type, String titre, String description) {
        TypeMedia typeMedia = parseType(type);
        if (typeMedia == TypeMedia.VISITE_VIRTUELLE) {
            throw new BadRequestException("Une visite virtuelle s'ajoute avec un lien, pas avec un fichier");
        }
        if (fichier == null || fichier.isEmpty()) {
            throw new BadRequestException("Le fichier est obligatoire");
        }
        if (fichier.getSize() > TAILLE_MAX_OCTETS) {
            throw new BadRequestException("Le fichier dépasse la taille maximale autorisée (25 Mo)");
        }
        String typeContenu = fichier.getContentType();
        String extension = typeContenu == null ? null : EXTENSIONS.get(typeContenu.toLowerCase());
        if (extension == null) {
            throw new BadRequestException("Format non supporté : utilisez une image JPEG, PNG ou WebP");
        }

        byte[] entete;
        try (InputStream in = fichier.getInputStream()) {
            entete = in.readNBytes(12);
        } catch (IOException e) {
            throw new BadRequestException("Lecture du fichier impossible");
        }
        if (!signatureValide(entete, extension)) {
            throw new BadRequestException("Le contenu du fichier ne correspond pas au format annoncé");
        }

        String nomFichier = UUID.randomUUID() + "." + extension;
        try {
            Path dossier = dossierMedias();
            Files.createDirectories(dossier);
            try (InputStream in = fichier.getInputStream()) {
                Files.copy(in, dossier.resolve(nomFichier), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Enregistrement du fichier impossible", e);
        }

        return MediaFoncier.builder()
                .type(typeMedia)
                .titre(nettoyer(titre))
                .description(nettoyer(description))
                .nomFichier(nomFichier)
                .typeContenu(typeContenu.toLowerCase())
                .build();
    }

    private MediaFoncier construireDepuisLien(MediaLienRequest request) {
        return MediaFoncier.builder()
                .type(TypeMedia.VISITE_VIRTUELLE)
                .titre(nettoyer(request.getTitre()))
                .description(nettoyer(request.getDescription()))
                .lienExterne(request.getLienExterne().trim())
                .build();
    }

    private TypeMedia parseType(String type) {
        if (type == null || type.isBlank()) {
            throw new BadRequestException("Le type de média est obligatoire (PHOTO, PANORAMA_360 ou VISITE_VIRTUELLE)");
        }
        try {
            return TypeMedia.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Type de média invalide : " + type);
        }
    }

    private boolean signatureValide(byte[] b, String extension) {
        if (b.length < 12) {
            return false;
        }
        return switch (extension) {
            case "jpg" -> (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case "png" -> (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
            case "webp" -> b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }

    private Path dossierMedias() {
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve("medias");
    }

    private String nettoyer(String valeur) {
        if (valeur == null) {
            return null;
        }
        String v = valeur.trim();
        return v.isEmpty() ? null : v;
    }

    private ProgrammeFoncier findProgramme(Long id) {
        return programmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programme foncier introuvable avec l'id : " + id));
    }

    private BienFoncier findBien(Long id) {
        return bienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bien foncier introuvable avec l'id : " + id));
    }

    private MediaFoncier findMedia(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Média introuvable avec l'id : " + id));
    }

    private MediaFoncierResponse mapToResponse(MediaFoncier m) {
        return MediaFoncierResponse.builder()
                .id(m.getId())
                .type(m.getType())
                .titre(m.getTitre())
                .description(m.getDescription())
                .url(m.getNomFichier() != null ? PREFIXE_URL + m.getNomFichier() : m.getLienExterne())
                .typeContenu(m.getTypeContenu())
                .ordre(m.getOrdre())
                .dateAjout(m.getDateAjout())
                .programmeId(m.getProgrammeFoncier() != null ? m.getProgrammeFoncier().getId() : null)
                .bienId(m.getBienFoncier() != null ? m.getBienFoncier().getId() : null)
                .build();
    }
}
