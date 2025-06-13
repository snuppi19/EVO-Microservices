package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.infrastructure.persistance.entity.Role.UserRoleEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleDomainRepository {
    UserRole save(UserRole userRole);
    List<UserRole> findByUserId(Integer userId);
    boolean existsByUserIdAndRoleId(Integer userId, Integer roleId);
    void deleteByUserIdAndRoleId(Integer userId, Integer roleId);

    @Query("SELECT ur.roleId FROM UserRoleEntity ur WHERE ur.userId = :userId")
    List<Integer>findRoleIdsByUserId(Integer userId);
}
