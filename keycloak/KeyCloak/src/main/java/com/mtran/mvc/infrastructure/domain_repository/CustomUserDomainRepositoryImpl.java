package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.CustomUser;
import com.mtran.mvc.domain.repository.CustomUserDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.CustomUserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.CustomUserRepository;
import com.mtran.mvc.infrastructure.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CustomUserDomainRepositoryImpl implements CustomUserDomainRepository {
   private final CustomUserRepository customUserRepository;
   private final DomainMapper domainMapper;
    @Override
    public boolean existsByUsername(String username) {
        return customUserRepository.existsByUsername(username);
    }

    @Override
    public List<CustomUser> findAllBysth(Specification<CustomUserEntity> a) {
        return customUserRepository.findAll(a).stream().map(domainMapper::toCustomUser).collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<CustomUserEntity> a) {
        customUserRepository.saveAll(a);
    }
}
