package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.infrastructure.persistance.entity.Role.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
    List<UserRoleEntity> findByUserId(Integer userId);
    boolean existsByUserIdAndRoleId(Integer userId, Integer roleId);
    void deleteByUserIdAndRoleId(Integer userId, Integer roleId);
    @Query("SELECT ur.roleId FROM UserRoleEntity ur WHERE ur.userId = :userId")
    List<Integer>findRoleIdsByUserId(Integer userId);

}
