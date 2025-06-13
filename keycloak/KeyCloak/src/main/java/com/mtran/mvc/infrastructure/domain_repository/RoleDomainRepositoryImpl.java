package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import com.mtran.mvc.infrastructure.persistance.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleDomainRepositoryImpl implements RoleDomainRepository {
    private final RoleRepository roleRepository;
    private final DomainMapper domainMapper;

    @Override
    public Role save(RoleEntity role) {
        return domainMapper.toDomainRole(roleRepository.save(role));
    }

    @Override
    public Role findById(int id) {
        return domainMapper.toDomainRole(roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found: " + id)));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll().stream().map(domainMapper::toDomainRole).collect(Collectors.toList());
    }

    @Override
    public List<Role> findAllById(List<Integer> Ids) {
        return roleRepository.findAllById(Ids).stream().map(domainMapper::toDomainRole).collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        Optional<RoleEntity> roleEntity = roleRepository.findByRoleName(roleName);
        return roleEntity.map(domainMapper::toDomainRole);
    }

    @Override
    public Role findByRoleNameIgnoreCase(String roleName) {
        return domainMapper.toDomainRole(roleRepository.findByRoleNameIgnoreCase(roleName));
    }

    @Override
    public void deleteById(int id) {
     roleRepository.deleteById(id);
    }

}
