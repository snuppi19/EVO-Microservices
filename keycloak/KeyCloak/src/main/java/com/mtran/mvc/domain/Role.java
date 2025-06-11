package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveRoleCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Integer roleId;
    private String roleName;

    public Role(SaveRoleCmd command) {
        this.roleId = command.getRoleId();
        this.roleName = command.getRoleName();
    }
}
