package com.mtran.mvc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("API Gateway").version("v1.0.0").description("API Gateway for IAM and Storage services"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .authorizationUrl("http://localhost:8081/realms/dev/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8081/realms/dev/protocol/openid-connect/token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect scope")
                                                        .addString("profile", "User profile scope"))))))
                .addSecurityItem(new SecurityRequirement().addList("oauth2", Arrays.asList("openid", "profile")));
    }
}