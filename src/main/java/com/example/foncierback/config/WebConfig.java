package com.example.foncierback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Expose les médias enregistrés sur disque sous /uploads/medias/**. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dossier = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("medias");
        try {
            Files.createDirectories(dossier);
        } catch (IOException ignored) {
            // Le dossier sera recréé lors du premier envoi.
        }
        String emplacement = dossier.toUri().toString();
        if (!emplacement.endsWith("/")) {
            emplacement += "/";
        }
        registry.addResourceHandler("/uploads/medias/**")
                .addResourceLocations(emplacement)
                .setCachePeriod(3600);
    }
}
