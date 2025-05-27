package com.mtran.mvc.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    Integer roleId;
    String roleName;
    private List<PermissionDto> permissions;
}
