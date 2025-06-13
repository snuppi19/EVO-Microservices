package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Integer> {
    List<RolePermissionEntity> findByRoleId(Integer roleId);
    List<RolePermissionEntity> findByRoleIdIn(List<Integer> roleIds);
    @Query("SELECT rp.permissionId FROM RolePermissionEntity rp WHERE rp.roleId = :roleId")
    List<Integer> findPermissionIdsByRoleId(Integer roleId);
    boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId);
}
