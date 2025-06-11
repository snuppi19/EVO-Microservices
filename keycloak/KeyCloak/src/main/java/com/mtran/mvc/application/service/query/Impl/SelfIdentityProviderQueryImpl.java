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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelfIdentityProviderQueryImpl implements IdentityProvidersQuery {
   private final TokenQueryService tokenQueryService;
   private final UserMapperKeycloak userMapperKeycloak;
   private final DomainMapper domainMapper;
   private final UserDomainRepository userDomainRepository;

    @Override
    public String getLoginUrl() {
        return null;
    }


    @Override
    public TokenResponse getTokensAfterLogin(String email) {
        String accessToken = tokenQueryService.getAccessToken(email);
        String refreshToken = tokenQueryService.getRefreshToken(email);
        if (accessToken == null || refreshToken == null) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public UserResponse getUserProfile(String email) {
        User user = userDomainRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userMapperKeycloak.toUserResponse(domainMapper.toEntityUser(user));
    }

}
