package com.example.schoolgestapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour Swagger/OpenAPI.
 * Cette classe centralise la documentation d'API métier de SchoolGest.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Filtre les APIs pour ne montrer que les routes métier (/api/**).
     * Exclut automatiquement les controllers de Spring Data REST et les interfaces système.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Services-Metier-ERP")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI schoolGestOpenAPI() {
        Contact contact = new Contact()
                .name("Assistance Technique SchoolGest")
                .email("support@schoolgest.com")
                .url("https://schoolgest.com/support");

        License license = new License()
                .name("Propriété Exclusive SchoolGest")
                .url("https://schoolgest.com/legal");

        Info info = new Info()
                .title("SchoolGest ERP - API Gateway")
                .description("### Plateforme de Gestion Scolaire Intégrée\n" +
                        "Interface de communication entre le Backend ERP et les applications Frontend.\n\n" +
                        "**Fonctionnalités incluses :**\n" +
                        "- Authentification Sécurisée (JWT)\n" +
                        "- Gestion du Parcours Académique\n" +
                        "- Suivi de présences et d'évaluations\n" +
                        "- Espace Travaux et Ressources Numériques\n" +
                        "- Administration et Imports de masse\n\n" +
                        "*Utilisez le bouton 'Authorize' pour injecter votre jeton d'accès.*")
                .version("1.5.0")
                .contact(contact)
                .license(license);

        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme);

        return new OpenAPI()
                .components(components)
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
