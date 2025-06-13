package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.repository.PermissionDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PermissionDomainRepositoryImpl implements PermissionDomainRepository {
    private final PermissionRepository permissionRepository;
    private final DomainMapper domainMapper;

    @Override
    public PermissionEntity findByResourceCodeAndScope(String resourceCode, String scope) {
        return permissionRepository.findByResourceCodeAndScope(resourceCode, scope);
    }

    @Override
    public List<Permission> findAllById(List<Integer> permissionIds) {
        return permissionRepository.findAllById(permissionIds).stream().map(domainMapper::toPermissionDomain).collect(Collectors.toList());
    }

    @Override
    public Permission findById(Integer id) {
        return permissionRepository.findById(id).map(domainMapper::toPermissionDomain).orElse(null);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll().stream().map(domainMapper::toPermissionDomain).collect(Collectors.toList());
    }

    @Override
    public Permission save(Permission permission) {
        return domainMapper.toPermissionDomain(permissionRepository.save(domainMapper.toEntityPermission(permission)));
    }

    @Override
    public void deleteById(Integer id) {
        permissionRepository.deleteById(id);
    }
}
