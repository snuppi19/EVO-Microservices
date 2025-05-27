package com.mtran.mvc.repository;

import com.mtran.mvc.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long>, JpaSpecificationExecutor<CustomUser> {
    boolean existsByUsername(String username);
}
