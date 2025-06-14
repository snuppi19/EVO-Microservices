package com.mtran.mvc.infrastructure.config.role_permission_default;

import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import com.mtran.mvc.infrastructure.persistance.repository.PermissionRepository;
import com.mtran.mvc.infrastructure.persistance.repository.RolePermissionRepository;
import com.mtran.mvc.infrastructure.persistance.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultRolePermission {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @PostConstruct
    public void init() {
        // Tạo quyền cho resource = 'user'
        createPermissionIfNotExists("user", "create");
        createPermissionIfNotExists("user", "update");
        createPermissionIfNotExists("user", "view");
        createPermissionIfNotExists("user", "delete");

        // Tạo quyền cho resource = 'role'
        createPermissionIfNotExists("role", "create");
        createPermissionIfNotExists("role", "update");
        createPermissionIfNotExists("role", "view");
        createPermissionIfNotExists("role", "delete");

        // Tạo quyền cho resource = 'permission'
        createPermissionIfNotExists("permission", "create");
        createPermissionIfNotExists("permission", "update");
        createPermissionIfNotExists("permission", "view");
        createPermissionIfNotExists("permission", "delete");

        // Tạo quyền cho resource = 'file'
        createPermissionIfNotExists("file", "upload");
        createPermissionIfNotExists("file", "view");
        createPermissionIfNotExists("file", "download");
        createPermissionIfNotExists("file", "update");
        createPermissionIfNotExists("file", "delete");

        // Tạo quyền cho resource = 'contract_file' (ví dụ private file)/ hợp đồng
        createPermissionIfNotExists("contract_file", "upload");
        createPermissionIfNotExists("contract_file", "view");
        createPermissionIfNotExists("contract_file", "download");

        createPermissionIfNotExists("all_file", "view");

        // Tạo vai trò
        RoleEntity userManager = createRoleIfNotExists("USER");
        RoleEntity systemAdmin = createRoleIfNotExists("ADMIN");
        RoleEntity staff = createRoleIfNotExists("STAFF");

        // Gán quyền cho vai trò USER
        assignPermissionToRole(userManager.getRoleId(), "user", "view");
        assignPermissionToRole(userManager.getRoleId(), "user", "update");
        assignPermissionToRole(userManager.getRoleId(), "file", "upload");
        assignPermissionToRole(userManager.getRoleId(), "file", "view");
        assignPermissionToRole(userManager.getRoleId(), "file", "download");

        // Gán quyền cho vai trò ADMIN
        assignPermissionToRole(systemAdmin.getRoleId(), "user", "create");
        assignPermissionToRole(systemAdmin.getRoleId(), "user", "update");
        assignPermissionToRole(systemAdmin.getRoleId(), "user", "view");
        assignPermissionToRole(systemAdmin.getRoleId(), "user", "delete");
        assignPermissionToRole(systemAdmin.getRoleId(), "role", "create");
        assignPermissionToRole(systemAdmin.getRoleId(), "role", "update");
        assignPermissionToRole(systemAdmin.getRoleId(), "role", "view");
        assignPermissionToRole(systemAdmin.getRoleId(), "role", "delete");
        assignPermissionToRole(systemAdmin.getRoleId(), "permission", "create");
        assignPermissionToRole(systemAdmin.getRoleId(), "permission", "update");
        assignPermissionToRole(systemAdmin.getRoleId(), "permission", "view");
        assignPermissionToRole(systemAdmin.getRoleId(), "permission", "delete");
        assignPermissionToRole(systemAdmin.getRoleId(), "file", "upload");
        assignPermissionToRole(systemAdmin.getRoleId(), "file", "view");
        assignPermissionToRole(systemAdmin.getRoleId(), "file", "download");
        assignPermissionToRole(systemAdmin.getRoleId(), "file", "update");
        assignPermissionToRole(systemAdmin.getRoleId(), "file", "delete");
        assignPermissionToRole(systemAdmin.getRoleId(), "contract_file", "upload");
        assignPermissionToRole(systemAdmin.getRoleId(), "contract_file", "view");
        assignPermissionToRole(systemAdmin.getRoleId(), "contract_file", "download");
        assignPermissionToRole(systemAdmin.getRoleId(), "all_file", "view");

        // Gán quyền cho vai trò STAFF
        assignPermissionToRole(staff.getRoleId(), "user", "view");
        assignPermissionToRole(staff.getRoleId(), "user", "update");
        assignPermissionToRole(staff.getRoleId(), "user", "delete");
        assignPermissionToRole(staff.getRoleId(), "file", "upload");
        assignPermissionToRole(staff.getRoleId(), "file", "view");
        assignPermissionToRole(staff.getRoleId(), "file", "download");
        assignPermissionToRole(staff.getRoleId(), "contract_file", "upload");
        assignPermissionToRole(staff.getRoleId(), "contract_file", "view");
        assignPermissionToRole(staff.getRoleId(), "contract_file", "download");
    }

    private void createPermissionIfNotExists(String resourceCode, String scope) {
        PermissionEntity permissionEntity = permissionRepository.findByResourceCodeAndScope(resourceCode, scope);
        if (permissionEntity == null) {
            PermissionEntity permissionEntityNew = new PermissionEntity();
            permissionEntityNew.setResourceCode(resourceCode);
            permissionEntityNew.setScope(scope);
            permissionRepository.save(permissionEntityNew);
        }
    }

    private RoleEntity createRoleIfNotExists(String roleName) {
        Optional<RoleEntity> existingRole = roleRepository.findByRoleName(roleName);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(roleName);
        return roleRepository.save(roleEntity);
    }

    private void assignPermissionToRole(Integer roleId, String resourceCode, String scope) {
        PermissionEntity permissionEntity = permissionRepository.findByResourceCodeAndScope(resourceCode, scope);
        if (permissionEntity == null) {
            throw new RuntimeException("Permission not found for resource: " + resourceCode + ", scope: " + scope);
        }
        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionEntity.getPermissionId())) {
            RolePermissionEntity rp = new RolePermissionEntity();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionEntity.getPermissionId());
            rolePermissionRepository.save(rp);
        }
    }
}