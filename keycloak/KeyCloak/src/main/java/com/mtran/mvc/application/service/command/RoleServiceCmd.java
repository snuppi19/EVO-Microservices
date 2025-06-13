package com.mtran.mvc.application.service.command;

import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleServiceCmd {
    Role save(Role role);
    ResponseEntity<?> update(Integer id, Role updatedRole);
    void deleteById(Integer id);
    List<Permission> getPermissionsByRoleId(Integer roleId);
    void addPermissionToRole(Integer roleId, Integer permissionId);
    void removePermissionFromRole(Integer roleId, Integer permissionId);
}
