package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.User;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


public interface UserDomainRepository {
    User save(User user);
    User findByKeycloakId(String keycloakId);
    Page<User> findAll(Pageable pageable);
    User findByEmail(String email);
    User findById(int id);
    void delete(User user);
}
