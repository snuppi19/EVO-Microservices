package com.mtran.mvc.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    private Integer permissionId;
    private String resourceCode;
    private String scope;
}
