package com.mtran.mvc.application.service.query;

import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleServiceQuery {
    List<Role> findAll();
    Optional<Role> findById(Integer id);
    List<Permission> getPermissionsByRoleId(Integer roleId);
}
