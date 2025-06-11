package com.mtran.mvc.application.service.query.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.mapper.UserMapperKeycloak;
import com.mtran.mvc.application.dto.response.TokenResponse;
import com.mtran.mvc.application.dto.response.UserResponse;
import com.mtran.mvc.application.service.query.IdentityProvidersQuery;
import com.mtran.mvc.application.service.query.TokenQueryService;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakIdentityProviderQueryImpl implements IdentityProvidersQuery {
    private final KeycloakProperties keycloakProperties;
    private final UserDomainRepository userDomainRepository;
    private final TokenQueryService tokenQueryService;
    private final DomainMapper domainMapper;
    private final UserMapperKeycloak userMapperKeycloak;

    @Override
    public String getLoginUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=openid",
                keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getRealm(),
                keycloakProperties.getClientId(),
                keycloakProperties.getRedirectUri());
    }


    @Override
    public TokenResponse getTokensAfterLogin(String email) {
        throw new UnsupportedOperationException("Not supported yet, method getTokensAfterLogin used for SelfIdentity");
    }

    @Override
    public UserResponse getUserProfile(String keycloakId) {
        String keycloakIdCheck = SecurityContextHolder.getContext().getAuthentication().getName();
        User userEntityCheck = userDomainRepository.findByKeycloakId(keycloakIdCheck);
        String accessToken = tokenQueryService.getAccessToken(userEntityCheck.getEmail());
        if (accessToken == null) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        User user = userDomainRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userMapperKeycloak.toUserResponse(domainMapper.toEntityUser(user));
    }
}
