package com.mtran.mvc.application.service.query.Impl;

import com.mtran.mvc.application.service.query.RoleServiceQuery;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.RolePermission;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.repository.PermissionDomainRepository;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.domain.repository.RolePermissionDomainRepository;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceQueryImpl implements RoleServiceQuery {
    private final RoleDomainRepository roleDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final RolePermissionDomainRepository rolePermissionDomainRepository;
    private final PermissionDomainRepository permissionDomainRepository;
    private final DomainMapper domainMapper;
    @Override
    public List<Role> findAll() {
        return roleDomainRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Integer id) {
        return Optional.ofNullable(roleDomainRepository.findById(id));
    }
    public List<String> getRolesByUserId(int userId) {
        return userRoleDomainRepository.findByUserId(userId)
                .stream()
                .map(userRole -> roleDomainRepository.findById(userRole.getRoleId()).getRoleName())
                .collect(Collectors.toList());
    }

    // Lấy danh sách quyền của user dựa trên userId
    public List<Permission> getPermissionsByUserId(Integer userId) {
        // Lấy danh sách role_id của user
        List<Integer> roleIds = userRoleDomainRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // Lấy danh sách permission_id từ bảng role_permission dựa trên danh sách role_id
        List<Integer> permissionIds = rolePermissionDomainRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        // Trả về danh sách Permission
        return permissionDomainRepository.findAllById(permissionIds);
    }


    public List<Permission> getPermissionsByRoleName(String roleName) {
        Role role = roleDomainRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        List<RolePermission> rolePermissionEntities = rolePermissionDomainRepository.findByRoleId(role.getRoleId());
        List<Integer> permissionIds = rolePermissionEntities.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        return permissionDomainRepository.findAllById(permissionIds);
    }
    @Override
    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        List<RolePermission> rolePermissionEntities = rolePermissionDomainRepository.findByRoleId(roleId);
        List<Permission> permissionEntityList = rolePermissionEntities.stream()
                //biến rolepermiss thành permission nhờ logic tìm ra perrmission từ permissionId có trong rolepermission
                .map(rolePermissionEntity -> permissionDomainRepository.findById(rolePermissionEntity.getRolePermissionId()))
                //lọc từ all permission thành những thằng khác null
                .filter(permission -> permission != null)
                .collect(Collectors.toList());
        return permissionEntityList;
    }
}
