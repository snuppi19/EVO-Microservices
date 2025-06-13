package com.mtran.mvc.application.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class RefreshRequest {
    private String email;
    private String refreshToken;
}
