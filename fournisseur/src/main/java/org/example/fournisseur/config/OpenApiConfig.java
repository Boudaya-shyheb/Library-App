package org.example.fournisseur.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fournisseurOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Fournisseur - Gestion Bibliothèque")
                        .description("Documentation de l'API Fournisseur")
                        .version("v1")
                        .contact(new Contact()
                                .name("Équipe Bibliothèque")
                                .email("contact@bibliotheque.local"))
                        .license(new License().name("Propriétaire")));
    }
}
