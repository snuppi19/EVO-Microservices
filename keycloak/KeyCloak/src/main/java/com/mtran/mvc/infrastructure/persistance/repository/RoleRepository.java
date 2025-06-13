package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByRoleName(String roleName);
    RoleEntity findByRoleNameIgnoreCase(String roleName);
}
