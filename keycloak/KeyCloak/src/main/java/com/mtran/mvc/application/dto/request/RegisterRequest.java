package com.mtran.mvc.application.dto.request;

import lombok.*;

@Data
@Setter
@Getter
public class RegisterRequest {
    private String Id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
}
