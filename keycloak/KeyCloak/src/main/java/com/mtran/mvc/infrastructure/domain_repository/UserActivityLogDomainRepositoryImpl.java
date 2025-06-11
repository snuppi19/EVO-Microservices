package com.mtran.mvc.infrastructure.domain_repository;


import com.mtran.mvc.domain.UserActivityLog;
import com.mtran.mvc.domain.repository.UserActivityLogDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.repository.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserActivityLogDomainRepositoryImpl implements UserActivityLogDomainRepository {
    private final UserActivityLogRepository userActivityLogRepository;
    private final DomainMapper domainMapper;

    @Override
    public List<UserActivityLog> findByEmail(String email) {
        return userActivityLogRepository.findByEmail(email).stream()
                .map(ual -> domainMapper.toUserActivityLog(ual))
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserActivityLog userActivityLog) {
        userActivityLogRepository.save(domainMapper.toUserActivityLogEntity(userActivityLog));
    }
}
