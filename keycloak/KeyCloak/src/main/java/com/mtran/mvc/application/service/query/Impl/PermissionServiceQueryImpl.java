package com.mtran.mvc.application.service.query.Impl;

import com.mtran.mvc.application.service.query.PermissionServiceQuery;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.repository.PermissionDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionServiceQueryImpl implements PermissionServiceQuery {
   private final PermissionDomainRepository permissionDomainRepository;
   private final DomainMapper domainMapper;

    @Override
    public List<Permission> findAll() {
        return permissionDomainRepository.findAll();
    }

    @Override
    public Optional<Permission> findById(Integer id) {
        return Optional.ofNullable(permissionDomainRepository.findById(id));
    }
}
