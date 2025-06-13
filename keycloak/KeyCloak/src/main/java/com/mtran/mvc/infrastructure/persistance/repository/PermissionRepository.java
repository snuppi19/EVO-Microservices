package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {
    @Query("SELECT p FROM PermissionEntity p WHERE p.resourceCode = :resourceCode AND p.scope = :scope")
    PermissionEntity findByResourceCodeAndScope(String resourceCode, String scope);
}
