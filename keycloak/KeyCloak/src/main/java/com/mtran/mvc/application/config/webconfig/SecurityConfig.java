package com.mtran.mvc.application.config.webconfig;

import com.mtran.common.config.RolePermissionEvaluator;
import com.mtran.mvc.application.config.utils.jwt.JwtAuthenticationFilter;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakProperties keycloakProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RolePermissionEvaluator rolePermissionEvaluator;
    private final String[] PUBLIC_ENDPOINTS = {"/home/register", "/home/login", "/home/callback", "/home/logout"
            , "/home/verify-otp", "/swagger-ui.html", "/swagger-ui/**", "/iam/v3/api-docs/**","/v3/api-docs.yaml"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());


        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        // NẾU THUỘC TÍNH KEYCLOAK ENABLE XÁC ĐỊNH SỬ DỤNG OATH2 RESOURCE SERVER HOẶC JWTAUTHENTICATION FILTER

        if (keycloakProperties.isEnabled()) {
            httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwtConfigurer -> jwtConfigurer
                            .jwkSetUri(keycloakProperties.getAuthServerUrl() + "/realms/" +
                                    keycloakProperties.getRealm() + "/protocol/openid-connect/certs")
                            .jwtAuthenticationConverter(new JwtAuthenticationConverter())
                    ));
        } else {
            httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(rolePermissionEvaluator);
        return expressionHandler;
    }
}