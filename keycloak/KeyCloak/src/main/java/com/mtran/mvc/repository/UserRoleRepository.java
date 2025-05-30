package com.mtran.mvc.repository;

import com.mtran.mvc.entity.Role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByUserId(Integer userId);
    boolean existsByUserIdAndRoleId(Integer userId, Integer roleId);
    void deleteByUserIdAndRoleId(Integer userId, Integer roleId);
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Integer>findRoleIdsByUserId(Integer userId);

}
