package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Role.UserRoleEntity;
import com.mtran.mvc.infrastructure.persistance.repository.UserRepository;
import com.mtran.mvc.infrastructure.persistance.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRoleDomainRepositoryImpl implements UserRoleDomainRepository {
    private final UserRoleRepository userRoleRepository;
    private final DomainMapper domainMapper;

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleEntity a = domainMapper.toEntityUserRole(userRole);
        return domainMapper.toDomainUserRole(userRoleRepository.save(a));
    }

    @Override
    public List<UserRole> findByUserId(Integer userId) {
        List<UserRoleEntity> list = userRoleRepository.findByUserId(userId);
        return list.stream()
                .map(l -> domainMapper.toDomainUserRole(l))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserIdAndRoleId(Integer userId, Integer roleId) {
        return userRoleRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public void deleteByUserIdAndRoleId(Integer userId, Integer roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public List<Integer> findRoleIdsByUserId(Integer userId) {
        return userRoleRepository.findRoleIdsByUserId(userId);
    }
}
