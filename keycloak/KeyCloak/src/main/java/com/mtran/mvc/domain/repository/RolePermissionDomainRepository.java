package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.RolePermission;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolePermissionDomainRepository {
    List<RolePermission> findByRoleId(Integer roleId);
    List<RolePermission> findByRoleIdIn(List<Integer> roleIds);
    @Query("SELECT rp.permissionId FROM RolePermissionEntity rp WHERE rp.roleId = :roleId")
    List<Integer> findPermissionIdsByRoleId(Integer roleId);
    boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId);
    void save(RolePermission rolePermission);
    void delete(RolePermission rolePermission);
}
