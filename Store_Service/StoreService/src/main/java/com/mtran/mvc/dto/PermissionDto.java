package com.mtran.mvc.dto;

import lombok.Data;

@Data
public class PermissionDto {
    private Integer permissionId;
    private String resourceCode;
    private String scope;
}