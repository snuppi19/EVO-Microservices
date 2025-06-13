package com.mtran.mvc.application.service.command;

import com.mtran.mvc.domain.Permission;

public interface PermissionServiceCmd {
    Permission save(Permission permissionEntity);
    void deleteById(Integer id);
}
