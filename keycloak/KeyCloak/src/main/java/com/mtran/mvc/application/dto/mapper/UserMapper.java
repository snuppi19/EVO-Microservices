package com.mtran.mvc.application.dto.mapper;

import com.mtran.mvc.application.dto.request.RegisterRequest;
import com.mtran.mvc.application.dto.response.UserDTO;
import com.mtran.mvc.application.dto.response.UserResponse;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.mapstruct.Mapper;

//Class mapper dùng cho IAM service 1
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(UserEntity userEntity);
    UserEntity toUserEntity(UserDTO userDTO);
    UserResponse toUserResponse(User user);
}
