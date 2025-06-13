package com.mtran.mvc.application.mapper;

import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.domain.command.*;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RequestCmdMapper {
    SaveUserCmd toUserCmd(RegisterRequest request);
    UserLoginCmd toUserLoginCmd(LoginRequest request);
    RefreshCmd toRefreshCmd(RefreshRequest request);
    LogoutCmd toLogoutCmd(LogoutRequest request);
    ChangePassCmd toChangePassCmd(ChangePasswordRequest request);
    DeleteCmd toDeleteCmd(DeleteRequest request);
    ChangeActiveStatusCmd toChangeActiveStatusCmd(ChangeActiveStatusRequest request);
}
