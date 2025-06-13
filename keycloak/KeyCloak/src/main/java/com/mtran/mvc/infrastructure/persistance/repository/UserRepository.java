package com.mtran.mvc.infrastructure.persistance.repository;


import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
    UserEntity findById(int id);
    UserEntity findByKeycloakId(String keycloakId);
    Page<UserEntity> findAll(Pageable pageable);

}
