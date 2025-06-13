package com.mtran.mvc.application.dto.request;

import com.mtran.mvc.application.dto.response.UserDTO;
import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private String otp;
    private UserDTO userDTO;
    private Boolean isRegister;
}