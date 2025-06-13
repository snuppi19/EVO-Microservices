package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleDomainRepository {
    Role save(RoleEntity role);
    Role findById(int id);
    List<Role> findAll();
    List<Role>findAllById(List<Integer> Ids);
    Optional<Role> findByRoleName(String roleName);
    Role findByRoleNameIgnoreCase(String roleName);
    void deleteById(int id);
}
