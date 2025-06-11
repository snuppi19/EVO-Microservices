package com.mtran.mvc.application.service.command.Impl;

import com.mtran.mvc.application.service.command.PermissionServiceCmd;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.repository.PermissionDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceCmdImpl implements PermissionServiceCmd {
    private final PermissionDomainRepository permissionDomainRepository;

    @Override
    public Permission save(Permission permission) {
      return   permissionDomainRepository.save(permission);
    }

    @Override
    public void deleteById(Integer id) {
        permissionDomainRepository.deleteById(id);
    }
}
