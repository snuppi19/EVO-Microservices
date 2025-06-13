package com.mtran.mvc.application.dto.request;

import com.mtran.mvc.application.dto.response.UserDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    private UserDTO user;
    private String newPassword;
    private String token;
    private String refreshToken;
}
