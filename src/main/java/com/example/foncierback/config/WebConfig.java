package com.example.foncierback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Expose les médias généraux et les fichiers des modèles de maison enregistrés sur disque. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        register(registry, root.resolve("medias"), "/uploads/medias/**");
        register(registry, root.resolve("modeles-maison"), "/uploads/modeles-maison/**");
    }

    private void register(ResourceHandlerRegistry registry, Path directory, String pattern) {
        try { Files.createDirectories(directory); } catch (IOException ignored) { }
        String location = directory.toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler(pattern).addResourceLocations(location).setCachePeriod(3600);
    }
}
