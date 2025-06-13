package com.mtran.mvc.infrastructure.persistance.repository;

import com.mtran.mvc.infrastructure.persistance.entity.UserActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLogEntity,Long> {
   List<UserActivityLogEntity> findByEmail(String email);
}
