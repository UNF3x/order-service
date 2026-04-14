package com.unf3x.project1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI project1OpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project1 Orders API")
                        .version("v1")
                        .description("REST API for managing orders")
                        .contact(new Contact()
                                .name("UNF3x")));
    }
}