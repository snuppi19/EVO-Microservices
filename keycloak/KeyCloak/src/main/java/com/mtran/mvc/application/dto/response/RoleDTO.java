package com.mtran.mvc.application.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    Integer roleId;
    String roleName;
    private List<PermissionDTO> permissions;
}
