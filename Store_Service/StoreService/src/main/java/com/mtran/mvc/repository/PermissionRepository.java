package com.mtran.mvc.repository;

import com.mtran.mvc.entity.Permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Permission findByResourceCodeAndScope(String resourceCode, String scope);
}