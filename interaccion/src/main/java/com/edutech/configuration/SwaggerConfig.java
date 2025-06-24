package com.edutech.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Interacción - Edutech")
                        .version("1.0.0")
                        .description("Microservicio de interacción - Generar comentarios y reseñas"))
                .addSecurityItem(new SecurityRequirement().addList("bearer"))
                .components(new Components()
                        .addSecuritySchemes("bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .servers(List.of(new Server().url("http://localhost:8080/api")));
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ↓aqui le da permiso a la api gateway para acceder a este microservicio desde el puerto 8080
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("http://localhost:8080") // Gateway
                .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");

        // ↓este es para mantener el poder acceder al auth directamente
        registry.addMapping("/doc/swagger/interaccion/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET");
    }
}
