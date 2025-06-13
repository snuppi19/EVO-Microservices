package com.mtran.mvc.domain.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveRolePermissionCmd {
    private Integer RolePermissionId;
    private Integer roleId;
    private Integer permissionId;

}
