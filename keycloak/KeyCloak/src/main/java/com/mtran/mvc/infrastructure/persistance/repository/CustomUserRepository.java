package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.domain.CustomUser;
import com.mtran.mvc.infrastructure.persistance.entity.CustomUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CustomUserRepository extends JpaRepository<CustomUserEntity, Long>, JpaSpecificationExecutor<CustomUserEntity> {
    boolean existsByUsername(String username);
}
