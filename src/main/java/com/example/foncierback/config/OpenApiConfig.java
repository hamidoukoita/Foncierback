package com.example.foncierback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foncierPlusOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Foncier+ API")
                        .version("1.0.0")
                        .description(
                                "API REST de la plateforme Foncier+ " +
                                        "de promotion et d'aménagement foncier."
                        )
                        .contact(
                                new Contact()
                                        .name("Équipe Foncier+")
                        )
                );
    }
}