package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.CustomUser;
import com.mtran.mvc.infrastructure.persistance.entity.CustomUserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomUserDomainRepository {
    boolean existsByUsername(String username);
    List<CustomUser> findAllBysth(Specification<CustomUserEntity> a);
    void saveAll(List<CustomUserEntity> a);
}
