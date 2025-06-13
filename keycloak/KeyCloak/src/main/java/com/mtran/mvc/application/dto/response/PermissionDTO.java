package com.mtran.mvc.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Integer permissionId;
    private String resourceCode;
    private String scope;
}
