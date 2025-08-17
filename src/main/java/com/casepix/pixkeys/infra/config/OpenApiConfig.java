package com.casepix.pixkeys.infra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "PIX Keys API",
        version = "v1",
        description = "API de cadastro de chaves PIX (case)",
        contact = @Contact(name = "Time PIX", email = "time.pix@exemplo.com"),
        license = @License(name = "MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "DEV")
    }
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi pixKeysGroup() {
        return GroupedOpenApi.builder()
            .group("pix-keys")
            .pathsToMatch("/chave-pix/**")
            .build();
    }
}
