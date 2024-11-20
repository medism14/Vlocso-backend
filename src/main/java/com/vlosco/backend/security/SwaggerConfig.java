package com.vlosco.backend.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vlosco API")
                        .version("1.0.0")
                        .description("API de l'application Vlosco permettant la gestion des utilisateurs et des transactions.")
                        .contact(new Contact()
                                .name("Vlosco")
                                .email("contact@vlosco.com")
                                .url("www.vlosco.com")));
    }
}