package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.RolePermission;
import com.mtran.mvc.domain.repository.RolePermissionDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.RolePermissionEntity;
import com.mtran.mvc.infrastructure.persistance.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RolePermissionDomainImpl implements RolePermissionDomainRepository {
   private final RolePermissionRepository rolePermissionRepository;
   private final DomainMapper domainMapper;

    @Override
    public List<RolePermission> findByRoleId(Integer roleId) {
        return rolePermissionRepository.findByRoleId(roleId)
                .stream().map(domainMapper::toRolePermission).collect(Collectors.toList());
    }

    @Override
    public List<RolePermission> findByRoleIdIn(List<Integer> roleIds) {
        return rolePermissionRepository.findByRoleIdIn(roleIds).stream().map(domainMapper::toRolePermission).collect(Collectors.toList());
    }

    @Override
    public List<Integer> findPermissionIdsByRoleId(Integer roleId) {
        return rolePermissionRepository.findPermissionIdsByRoleId(roleId);
    }

    @Override
    public boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId) {
        return rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId);
    }

    @Override
    public void save(RolePermission rolePermission) {
        rolePermissionRepository.save(domainMapper.toEntityRolePermission(rolePermission));
    }

    @Override
    public void delete(RolePermission rolePermission) {
        rolePermissionRepository.delete(domainMapper.toEntityRolePermission(rolePermission));
    }
}
