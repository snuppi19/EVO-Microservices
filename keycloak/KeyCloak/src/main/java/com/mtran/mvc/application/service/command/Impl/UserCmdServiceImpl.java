package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.service.command.IdentityProvidersCmd;
import com.mtran.mvc.application.service.command.UserCmdService;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCmdServiceImpl implements UserCmdService {
    private final KeycloakProperties keycloakProperties;
    private final UserDomainRepository userDomainRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final DomainMapper domainMapper;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final KeycloakIdentityProviderCmdImpl keycloakIdentityProviderCmd;
    private final SelfIdentityProviderCmdImpl selfIdentityProviderCmd;


    private IdentityProvidersCmd getIdentityProviderCmd() {
        return keycloakProperties.isEnabled() ? keycloakIdentityProviderCmd : selfIdentityProviderCmd;
    }

    @Override
    public void register(RegisterRequest request) {
        getIdentityProviderCmd().register(request);
    }

    @Override
    public TokenExchangeResponse handleCallback(String code) {
        return getIdentityProviderCmd().handleCallback(code);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        getIdentityProviderCmd().login(loginRequest);
    }

    @Override
    public TokenExchangeResponse refresh(RefreshRequest refreshRequestKeyCloak) {
        return getIdentityProviderCmd().refresh(refreshRequestKeyCloak);
    }


    @Override
    public void logout(LogoutRequest request) {
        getIdentityProviderCmd().logout(request);
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        return getIdentityProviderCmd().changePassword(changePasswordRequest);
    }

    @Override
    public ResponseEntity<?> softDelete(DeleteRequest deleteRequest) {
        return getIdentityProviderCmd().softDelete(deleteRequest);
    }

    @Override
    public ResponseEntity<?> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest) {
        return getIdentityProviderCmd().changeActiveStatus(changeActiveStatusRequest);
    }

    @Override
    public void assignRoleToUser(Integer userId, Integer roleId) {
        validateUserRole(userId, roleId);
        if (!userRoleDomainRepository.existsByUserIdAndRoleId(userId, roleId)) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleDomainRepository.save(userRole);
        }
    }

    private void validateUserRole(Integer userId, Integer roleId) {
        User user = userDomainRepository.findById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Role role = roleDomainRepository.findById(roleId);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
    }

    @Override
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        validateUserRole(userId, roleId);
        if (userRoleDomainRepository.existsByUserIdAndRoleId(userId, roleId)) {
            userRoleDomainRepository.deleteByUserIdAndRoleId(userId, roleId);
        } else {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}
