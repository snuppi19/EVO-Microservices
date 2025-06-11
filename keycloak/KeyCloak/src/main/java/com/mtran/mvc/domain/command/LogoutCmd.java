package com.mtran.mvc.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LogoutCmd {
    String email;
    String token;
    String refreshToken;
}
