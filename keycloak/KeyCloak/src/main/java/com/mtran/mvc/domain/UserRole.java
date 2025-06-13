package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveUserRoleCmd;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    private Integer userRoleId;
    private Integer userId;
    private Integer roleId;

    public UserRole(SaveUserRoleCmd command) {
        this.roleId = command.getRoleId();
        this.userId = command.getUserId();
        this.userRoleId = command.getUserRoleId();
    }
}
