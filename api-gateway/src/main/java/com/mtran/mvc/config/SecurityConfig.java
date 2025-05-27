package com.mtran.mvc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/home/home/register", "/home/home/login", "/home/home/callback", "/home/home/logout",
            "/home/home/verify-otp",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**","/home/iam/v3/api-docs", "/storage/v3/api-docs"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwkSetUri("http://localhost:8081/realms/dev/protocol/openid-connect/certs"))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
}