package com.mtran.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            template.header("Authorization", "Bearer " + jwt.getTokenValue());
        }
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}