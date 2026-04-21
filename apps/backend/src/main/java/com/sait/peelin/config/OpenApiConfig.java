// Contributor(s): Robbie
// Main: Robbie - springdoc OpenAPI bean JWT security scheme and API metadata for Swagger UI.

package com.sait.peelin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link OpenAPI} bean for Swagger UI with bearer JWT security scheme and API metadata.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT from POST /api/v1/auth/login (paste token only in Authorize).")))
                .info(new Info()
                        .title("Peelin' Good API")
                        .version("1.0")
                        .description("REST API for bakery management catalog orders rewards and staff tools."));
    }
}
