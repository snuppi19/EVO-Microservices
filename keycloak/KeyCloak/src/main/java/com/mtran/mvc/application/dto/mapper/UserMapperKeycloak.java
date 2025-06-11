package com.mtran.mvc.application.dto.mapper;

import com.mtran.mvc.application.dto.request.RegisterRequest;
import com.mtran.mvc.application.dto.response.UserResponse;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.mapstruct.Mapper;
//Class này dùng cho keycloak
@Mapper(componentModel = "spring")
public interface UserMapperKeycloak {
    UserResponse toUserResponse(UserEntity userEntity);
    UserEntity toUser(RegisterRequest registerRequest);
}