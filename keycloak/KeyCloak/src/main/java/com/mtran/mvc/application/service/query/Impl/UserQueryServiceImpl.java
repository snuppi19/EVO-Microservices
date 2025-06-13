package com.mtran.mvc.application.service.query.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.mapper.UserMapper;
import com.mtran.mvc.application.dto.mapper.UserMapperKeycloak;
import com.mtran.mvc.application.dto.request.PagingRequest;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.dto.response.TokenResponse;
import com.mtran.mvc.application.dto.response.UserResponse;
import com.mtran.mvc.application.service.command.IdentityProvidersCmd;
import com.mtran.mvc.application.service.command.TokenCommandService;
import com.mtran.mvc.application.service.query.IdentityProvidersQuery;
import com.mtran.mvc.application.service.query.TokenQueryService;
import com.mtran.mvc.application.service.query.UserQueryService;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final KeycloakProperties keycloakProperties;
    private final UserDomainRepository userDomainRepository;
    private final UserMapper userMapper;
    private final DomainMapper domainMapper;
    private final UserMapperKeycloak userMapperKeycloak;
    private final TokenQueryService tokenQueryService;
    private final KeycloakIdentityProviderQueryImpl keycloakIdentityProviderQuery;
    private final SelfIdentityProviderQueryImpl selfIdentityProviderQuery;

    @Override
    public Page<UserResponse> getAllProfiles(PagingRequest pagingRequest) {
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDomainRepository.findByKeycloakId(keycloakId);
        String accessToken = tokenQueryService.getAccessToken(user.getEmail());
        if (accessToken == null) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        int page = pagingRequest.getPage();
        int size = pagingRequest.getSize();
        String sortBy = pagingRequest.getSortBy();
        boolean isDesc = pagingRequest.isDescending();

        Sort sort = isDesc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userDomainRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }

    private IdentityProvidersQuery getIdentityProviderQuery() {
        return keycloakProperties.isEnabled() ? keycloakIdentityProviderQuery : selfIdentityProviderQuery;
    }

    @Override
    public String getLoginUrl() {
        return getIdentityProviderQuery().getLoginUrl();
    }

    @Override
    public TokenResponse getTokensAfterLogin(String email) {
        return selfIdentityProviderQuery.getTokensAfterLogin(email);
    }

    @Override
    public UserResponse getUserProfileById(Integer id) {
        if (id <= 0) {
            throw new AppException(ErrorCode.ID_INVALID);
        }
        User user = userDomainRepository.findById(id);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (keycloakProperties.isEnabled()) {
            return getIdentityProviderQuery().getUserProfile(user.getKeycloakId());
        }
        return userMapperKeycloak.toUserResponse(domainMapper.toEntityUser(user));
    }
}
