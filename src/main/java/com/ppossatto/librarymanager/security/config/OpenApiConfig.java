package com.ppossatto.librarymanager.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
       .info(
          new Info()
             .title("Library Manager")
             .version("1.0.0")
             .description("""
                A simple Library Management API Documentation
                """)
       )
       .components(
          new Components()
             .addSecuritySchemes(
                "bearer-jwt", new SecurityScheme()
                   .type(SecurityScheme.Type.HTTP)
                   .scheme("bearer")
                   .bearerFormat("JWT")
                   .in(SecurityScheme.In.HEADER)
                   .name("Authorization")
             )
       )
       .addSecurityItem(
          new SecurityRequirement()
             .addList("bearer-jwt")
       );
  }
}
