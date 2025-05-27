package com.mtran.common.config;



import com.mtran.common.IdentityClient.IamFeignClient;
import com.mtran.common.dto.RoleDto;
import com.mtran.common.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RolePermissionEvaluator implements PermissionEvaluator {
    private final IamFeignClient iamFeignClient;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String resource = (String) targetDomainObject;
        String scope = (String) permission;

        String keycloakId = authentication.getName();
        UserDTO user = iamFeignClient.getUser(keycloakId);
        if (user == null) {
            return false;
        }

        List<RoleDto> roles = iamFeignClient.getUserRoles(keycloakId);
        if (roles.isEmpty()) {
            return false;
        }

        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getResourceCode().equals(resource) && p.getScope().equals(scope));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}