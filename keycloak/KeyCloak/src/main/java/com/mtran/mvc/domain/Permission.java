package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SavePermissionCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private Integer permissionId;
    private String resourceCode;
    private String scope;

    public Permission(SavePermissionCmd command) {
        this.permissionId = command.getPermissionId();
        this.resourceCode = command.getResourceCode();
        this.scope = command.getScope();
    }
}
