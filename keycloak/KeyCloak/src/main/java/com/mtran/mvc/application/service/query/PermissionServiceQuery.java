package com.mtran.mvc.application.service.query;

import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;

import java.util.List;
import java.util.Optional;

public interface PermissionServiceQuery {
    List<Permission> findAll();
    Optional<Permission> findById(Integer id);
}
