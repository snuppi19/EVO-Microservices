package com.mtran.mvc.infrastructure.mapper;

import com.mtran.mvc.domain.*;
import com.mtran.mvc.infrastructure.persistance.entity.CustomUserEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.UserRoleEntity;
import com.mtran.mvc.infrastructure.persistance.entity.UserActivityLogEntity;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DomainMapper {
    User toDomainUser(UserEntity user);
    UserEntity toEntityUser(User user);
    Role toDomainRole(RoleEntity role);
    RoleEntity toEntityRole(Role role);
    UserActivityLog toUserActivityLog(UserActivityLogEntity userActivityLog);
    UserActivityLogEntity toUserActivityLogEntity(UserActivityLog userActivityLog);
    UserRole toDomainUserRole(UserRoleEntity userRole);
    UserRoleEntity toEntityUserRole(UserRole userRole);
    RolePermission toRolePermission(RolePermissionEntity rolePermission);
    RolePermissionEntity toEntityRolePermission(RolePermission rolePermission);
    Permission toPermissionDomain(PermissionEntity permission);
    PermissionEntity toEntityPermission(Permission permission);
    CustomUser toCustomUser(CustomUserEntity user);

}
