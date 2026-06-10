package com.portfolio.pokedex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Pokedex API")
                .description("REST API built with Spring Boot that wraps PokeAPI — supports Pokemon listing, search, favorites and teams.")
                .version("1.0.0")
                .contact(new Contact().name("Portfolio").url("https://vercel.app")));
    }
}
