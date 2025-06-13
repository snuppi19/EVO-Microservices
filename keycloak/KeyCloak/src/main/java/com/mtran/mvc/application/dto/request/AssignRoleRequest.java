package com.mtran.mvc.application.dto.request;

import lombok.Data;

@Data
public class AssignRoleRequest {
    private Integer userId;
    private Integer roleId;
}