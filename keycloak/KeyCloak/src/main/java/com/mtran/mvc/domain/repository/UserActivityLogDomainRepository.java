package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.UserActivityLog;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserActivityLogDomainRepository {
    List<UserActivityLog> findByEmail(String email);
    void save(UserActivityLog userActivityLog);
}
