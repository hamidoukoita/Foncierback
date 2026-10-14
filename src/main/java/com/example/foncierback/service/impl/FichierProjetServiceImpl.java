package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.response.FichierProjetResponse;
import com.example.foncierback.entity.FichierProjet;
import com.example.foncierback.entity.ProjetConstruction;
import com.example.foncierback.entity.enums.StatutProjet;
import com.example.foncierback.entity.enums.TypeFichierProjet;
import com.example.foncierback.repository.FichierProjetRepository;
import com.example.foncierback.repository.ProjetConstructionRepository;
import com.example.foncierback.service.FichierProjetService;
import com.example.foncierback.service.ProjetConstructionAcces;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FichierProjetServiceImpl implements FichierProjetService {

    private static final long TAILLE_MAX_OCTETS = 15L * 1024 * 1024;
    private static final int NOMBRE_MAX_FICHIERS = 10;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "application/pdf", "pdf",
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final FichierProjetRepository fichierRepository;
    private final ProjetConstructionRepository projetRepository;
    private final ProjetConstructionAcces acces;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public FichierProjetResponse ajouter(Long projetId, TypeFichierProjet type, MultipartFile fichier) {
        ProjetConstruction projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet de construction introuvable avec l'identifiant : " + projetId));
        acces.verifierProprietaire(projet);
        if (projet.getStatut() != StatutProjet.EN_ETUDE) {
            throw new BadRequestException("Le dossier a déjà été traité : il ne peut plus être complété.");
        }
        if (fichier == null || fichier.isEmpty()) throw new BadRequestException("Le fichier est vide.");
        if (fichier.getSize() > TAILLE_MAX_OCTETS) throw new BadRequestException("Le fichier dépasse 15 Mo.");
        if (fichierRepository.countByProjetId(projetId) >= NOMBRE_MAX_FICHIERS) {
            throw new BadRequestException("Un dossier accepte au maximum " + NOMBRE_MAX_FICHIERS + " pièces jointes.");
        }
        String contentType = fichier.getContentType() == null ? "" : fichier.getContentType().toLowerCase();
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) throw new BadRequestException("Format non supporté : PDF, JPG, PNG ou WEBP uniquement.");

        String nom = UUID.randomUUID() + "." + extension;
        Path dossier = dossierProjet(projetId);
        try {
            Files.createDirectories(dossier);
            try (InputStream in = fichier.getInputStream()) {
                Files.copy(in, dossier.resolve(nom), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Enregistrement du fichier impossible", e);
        }

        FichierProjet enregistre = fichierRepository.save(FichierProjet.builder()
                .type(type)
                .nomOriginal(nomOriginal(fichier, extension))
                .nomFichier(nom)
                .contentType(contentType)
                .taille(fichier.getSize())
                .dateAjout(LocalDateTime.now())
                .projet(projet)
                .build());
        return versReponse(enregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public FichierProjet trouver(Long projetId, Long fichierId) {
        FichierProjet fichier = fichierRepository.findByIdAndProjetId(fichierId, projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe introuvable."));
        acces.verifierLecture(fichier.getProjet());
        return fichier;
    }

    @Override
    public Resource charger(FichierProjet fichier) {
        try {
            Path chemin = dossierProjet(fichier.getProjet().getId()).resolve(fichier.getNomFichier()).normalize();
            Resource resource = new UrlResource(chemin.toUri());
            if (!resource.exists()) throw new ResourceNotFoundException("Le fichier n'est plus disponible.");
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Le fichier n'est plus disponible.");
        }
    }

    @Override
    public void supprimer(Long projetId, Long fichierId) {
        FichierProjet fichier = fichierRepository.findByIdAndProjetId(fichierId, projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe introuvable."));
        acces.verifierProprietaire(fichier.getProjet());
        if (fichier.getProjet().getStatut() != StatutProjet.EN_ETUDE) {
            throw new BadRequestException("Le dossier a déjà été traité : ses pièces ne peuvent plus être modifiées.");
        }
        try {
            Files.deleteIfExists(dossierProjet(projetId).resolve(fichier.getNomFichier()));
        } catch (IOException ignored) {
            // Le fichier physique manquant n'empêche pas de retirer la pièce du dossier.
        }
        fichier.getProjet().getFichiers().remove(fichier);
        fichierRepository.delete(fichier);
    }

    public static FichierProjetResponse versReponse(FichierProjet f) {
        return FichierProjetResponse.builder()
                .id(f.getId())
                .type(f.getType())
                .nomOriginal(f.getNomOriginal())
                .contentType(f.getContentType())
                .taille(f.getTaille())
                .dateAjout(f.getDateAjout())
                .build();
    }

    /** Hors des dossiers servis publiquement (medias, modeles-maison). */
    private Path dossierProjet(Long projetId) {
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve("projets").resolve(String.valueOf(projetId));
    }

    private String nomOriginal(MultipartFile fichier, String extension) {
        String nom = fichier.getOriginalFilename();
        if (nom == null || nom.isBlank()) return "piece-jointe." + extension;
        String propre = Paths.get(nom).getFileName().toString();
        return propre.length() > 200 ? propre.substring(propre.length() - 200) : propre;
    }
}
