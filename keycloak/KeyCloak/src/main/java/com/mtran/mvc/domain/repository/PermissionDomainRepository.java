package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionDomainRepository {
    @Query("SELECT p FROM PermissionEntity p WHERE p.resourceCode = :resourceCode AND p.scope = :scope")
    PermissionEntity findByResourceCodeAndScope(String resourceCode, String scope);
    List<Permission> findAllById(List<Integer> permissionIds);
    Permission findById(Integer id);
    List<Permission> findAll();
    Permission save(Permission permission);
    void deleteById(Integer id);
}
