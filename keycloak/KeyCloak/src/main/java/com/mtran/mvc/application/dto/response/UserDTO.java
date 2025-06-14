package com.mtran.mvc.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String id;
    private String keycloakId;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
}
