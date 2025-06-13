package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveRolePermissionCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    private Integer rolePermissionId;
    private Integer roleId;
    private Integer permissionId;

    public RolePermission(SaveRolePermissionCmd command) {
        this.rolePermissionId = command.getRolePermissionId();
        this.roleId = command.getRoleId();
        this.permissionId = command.getPermissionId();
    }
}
