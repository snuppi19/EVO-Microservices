package com.mtran.mvc.domain.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveUserRoleCmd {
    private Integer userRoleId;
    private Integer userId;
    private Integer roleId;
}
