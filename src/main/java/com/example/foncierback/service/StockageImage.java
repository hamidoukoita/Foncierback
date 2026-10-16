package com.example.foncierback.service;

import com.example.foncierback.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Enregistre une image (JPEG, PNG, WebP) dans un sous-dossier de uploads/.
 * L'extension vient du type de contenu, et la signature du fichier est vérifiée :
 * le nom envoyé par le client n'est jamais utilisé.
 */
@Component
public class StockageImage {

    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    /** Enregistre l'image et retourne son nom de fichier (UUID + extension). */
    public String enregistrer(MultipartFile fichier, String sousDossier, long tailleMaxOctets) {
        if (fichier == null || fichier.isEmpty()) {
            throw new BadRequestException("Le fichier est obligatoire");
        }
        if (fichier.getSize() > tailleMaxOctets) {
            throw new BadRequestException("L'image dépasse la taille maximale autorisée ("
                    + tailleMaxOctets / (1024 * 1024) + " Mo)");
        }
        String typeContenu = fichier.getContentType();
        String extension = typeContenu == null ? null : EXTENSIONS.get(typeContenu.toLowerCase());
        if (extension == null) {
            throw new BadRequestException("Format non supporté : utilisez une image JPEG, PNG ou WebP");
        }
        try (InputStream in = fichier.getInputStream()) {
            if (!signatureValide(in.readNBytes(12), extension)) {
                throw new BadRequestException("Le contenu du fichier ne correspond pas au format annoncé");
            }
        } catch (IOException e) {
            throw new BadRequestException("Lecture du fichier impossible");
        }

        String nomFichier = UUID.randomUUID() + "." + extension;
        try (InputStream in = fichier.getInputStream()) {
            Path dossier = dossier(sousDossier);
            Files.createDirectories(dossier);
            Files.copy(in, dossier.resolve(nomFichier), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Enregistrement de l'image impossible", e);
        }
        return nomFichier;
    }

    /** Supprime un fichier précédemment enregistré ; un échec n'interrompt pas la requête. */
    public void supprimer(String sousDossier, String nomFichier) {
        if (nomFichier == null || nomFichier.isBlank()) {
            return;
        }
        try {
            Path cible = dossier(sousDossier).resolve(nomFichier).normalize();
            if (cible.startsWith(dossier(sousDossier))) {
                Files.deleteIfExists(cible);
            }
        } catch (IOException ignored) {
            // Fichier orphelin sans conséquence.
        }
    }

    private Path dossier(String sousDossier) {
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve(sousDossier);
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
}
