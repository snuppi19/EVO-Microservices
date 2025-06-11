package com.mtran.mvc.domain.command;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavePermissionCmd {
    private Integer permissionId;
    private String resourceCode;
    private String scope;
}
