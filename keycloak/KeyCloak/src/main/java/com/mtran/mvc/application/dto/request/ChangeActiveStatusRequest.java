package com.mtran.mvc.application.dto.request;


import com.mtran.mvc.application.dto.response.UserResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChangeActiveStatusRequest {
    private UserResponse user;
    private Boolean isActive;
}
