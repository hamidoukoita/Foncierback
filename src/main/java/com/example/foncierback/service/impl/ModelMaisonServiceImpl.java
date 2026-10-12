package com.example.foncierback.service.impl;

import com.example.foncierback.common.exception.BadRequestException;
import com.example.foncierback.common.exception.ResourceNotFoundException;
import com.example.foncierback.dto.request.ModelMaisonRequest;
import com.example.foncierback.dto.response.ModelMaisonResponse;
import com.example.foncierback.entity.ModelMaison;
import com.example.foncierback.entity.SocietePromotrice;
import com.example.foncierback.repository.ModelMaisonRepository;
import com.example.foncierback.repository.SocietePromotriceRepository;
import com.example.foncierback.service.ModelMaisonService;
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
public class ModelMaisonServiceImpl implements ModelMaisonService {

    private static final long MAX_BYTES = 25L * 1024 * 1024;
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );
    private static final Map<String, String> PLAN_EXTENSIONS = Map.of(
            "application/pdf", "pdf",
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final ModelMaisonRepository modelMaisonRepository;
    private final SocietePromotriceRepository societePromotriceRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public ModelMaisonResponse create(ModelMaisonRequest request) {
        if (modelMaisonRepository.existsByLibellerIgnoreCase(request.getLibeller())) {
            throw new BadRequestException("Un modèle de maison avec le libellé '" + request.getLibeller() + "' existe déjà");
        }
        validateRange(request.getSurfaceTerrainMin(), request.getSurfaceTerrainMax());
        SocietePromotrice societe = resolveSociete(request.getSocieteId());

        ModelMaison model = ModelMaison.builder()
                .libeller(request.getLibeller().trim())
                .description(clean(request.getDescription()))
                .imageUrl(clean(request.getImageUrl()))
                .planUrl(clean(request.getPlanUrl()))
                .surfaceTerrainMin(request.getSurfaceTerrainMin())
                .surfaceTerrainMax(request.getSurfaceTerrainMax())
                .surfaceConstruite(request.getSurfaceConstruite())
                .nombreChambres(request.getNombreChambres())
                .nombreSallesBain(request.getNombreSallesBain())
                .typeTerrainCompatible(clean(request.getTypeTerrainCompatible()))
                .societePromotrice(societe)
                .build();

        return mapToResponse(modelMaisonRepository.save(model));
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMaisonResponse getById(Long id) {
        return mapToResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMaisonResponse> getAll(Long societeId) {
        List<ModelMaison> models = societeId == null
                ? modelMaisonRepository.findAll()
                : modelMaisonRepository.findBySocietePromotriceIdOrSocietePromotriceIsNullOrderByLibellerAsc(societeId);
        return models.stream().map(this::mapToResponse).toList();
    }

    @Override
    public ModelMaisonResponse update(Long id, ModelMaisonRequest request) {
        ModelMaison existing = findEntityById(id);
        if (!existing.getLibeller().equalsIgnoreCase(request.getLibeller())
                && modelMaisonRepository.existsByLibellerIgnoreCase(request.getLibeller())) {
            throw new BadRequestException("Un modèle de maison avec le libellé '" + request.getLibeller() + "' existe déjà");
        }
        validateRange(request.getSurfaceTerrainMin(), request.getSurfaceTerrainMax());
        existing.setLibeller(request.getLibeller().trim());
        existing.setDescription(clean(request.getDescription()));
        existing.setImageUrl(clean(request.getImageUrl()));
        existing.setPlanUrl(clean(request.getPlanUrl()));
        existing.setSurfaceTerrainMin(request.getSurfaceTerrainMin());
        existing.setSurfaceTerrainMax(request.getSurfaceTerrainMax());
        existing.setSurfaceConstruite(request.getSurfaceConstruite());
        existing.setNombreChambres(request.getNombreChambres());
        existing.setNombreSallesBain(request.getNombreSallesBain());
        existing.setTypeTerrainCompatible(clean(request.getTypeTerrainCompatible()));
        if (request.getSocieteId() != null) existing.setSocietePromotrice(resolveSociete(request.getSocieteId()));
        return mapToResponse(modelMaisonRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        modelMaisonRepository.delete(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMaisonResponse> searchByLibeller(String keyword) {
        return modelMaisonRepository.findByLibellerContainingIgnoreCase(keyword == null ? "" : keyword)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMaisonResponse> findCompatibles(Double surfaceTerrain, String typeTerrain, Long societeId) {
        return modelMaisonRepository.findCompatibles(surfaceTerrain, clean(typeTerrain), societeId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public ModelMaisonResponse uploadImage(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("L'image est obligatoire");
        ModelMaison model = findEntityById(id);
        String url = saveFile(file, IMAGE_EXTENSIONS, "images");
        model.setImageUrl(url);
        return mapToResponse(modelMaisonRepository.save(model));
    }

    @Override
    public ModelMaisonResponse uploadPlan(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("Le plan est obligatoire");
        ModelMaison model = findEntityById(id);
        String url = saveFile(file, PLAN_EXTENSIONS, "plans");
        model.setPlanUrl(url);
        return mapToResponse(modelMaisonRepository.save(model));
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMaison findEntityById(Long id) {
        return modelMaisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modèle de maison introuvable avec l'identifiant : " + id));
    }

    private SocietePromotrice resolveSociete(Long id) {
        if (id == null) return null;
        return societePromotriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Société promotrice introuvable avec l'identifiant : " + id));
    }

    private void validateRange(Double min, Double max) {
        if (min != null && max != null && min > max) {
            throw new BadRequestException("La surface minimale du terrain ne peut pas dépasser la surface maximale");
        }
    }

    private String saveFile(MultipartFile file, Map<String, String> extensions, String subdir) {
        if (file.getSize() > MAX_BYTES) throw new BadRequestException("Le fichier dépasse 25 Mo");
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        String extension = extensions.get(contentType);
        if (extension == null) throw new BadRequestException("Format non supporté");

        String name = UUID.randomUUID() + "." + extension;
        Path directory = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("modeles-maison").resolve(subdir);
        try {
            Files.createDirectories(directory);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, directory.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Enregistrement du fichier impossible", e);
        }
        return "/uploads/modeles-maison/" + subdir + "/" + name;
    }

    private String clean(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private ModelMaisonResponse mapToResponse(ModelMaison entity) {
        return ModelMaisonResponse.builder()
                .id(entity.getId())
                .libeller(entity.getLibeller())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .planUrl(entity.getPlanUrl())
                .surfaceTerrainMin(entity.getSurfaceTerrainMin())
                .surfaceTerrainMax(entity.getSurfaceTerrainMax())
                .surfaceConstruite(entity.getSurfaceConstruite())
                .nombreChambres(entity.getNombreChambres())
                .nombreSallesBain(entity.getNombreSallesBain())
                .typeTerrainCompatible(entity.getTypeTerrainCompatible())
                .societeId(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getId() : null)
                .societeNom(entity.getSocietePromotrice() != null ? entity.getSocietePromotrice().getNom() : null)
                .build();
    }
}
