package com.example.Ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        SecurityScheme scheme=new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirement=new SecurityRequirement()
                .addList("Bearer Authentication");
        return new OpenAPI().
                info(new Info().title("Spring Boot Ecommerce APIs").version("1.0").description("This is a SpringBoot Project for Ecommerce")
                        .contact(new Contact().name("Vikas Paindla")
                                .email("vikaspaindla8008@gmail.com")
                                .url("https://github.com/vikaspaindla1999/")))
                .components(new Components().addSecuritySchemes("Bearer Authentication",scheme)).
                addSecurityItem(bearerRequirement);
    }
}
