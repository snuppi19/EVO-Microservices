package com.mtran.common.dto;

import lombok.Data;

@Data
public class PermissionDto {
    private Integer permissionId;
    private String resourceCode;
    private String scope;
}