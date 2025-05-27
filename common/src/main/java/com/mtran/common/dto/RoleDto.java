package com.mtran.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    private Integer roleId;
    private String roleName;
    private List<PermissionDto> permissions;
}