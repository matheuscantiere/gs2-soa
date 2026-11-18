package br.com.fiap3ess.rotaseca.config.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Rota Seca - Serviço de Risco da Rua")
                        .description("API aberta da CitizenTech Rota Seca: previsão de alagamento por cruzamento, "
                                + "confirmações do CGE, reportes do sensor de nível e chuva atual (Open-Meteo). "
                                + "Backend consumido pelo app mobile Rota Seca. Horários em ISO 8601 com -03:00.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rota Seca - FIAP Global Solution 2026.2")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                );
    }
}
