package com.mtran.mvc.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserLoginCmd {
    private String email;
    private String password;
}
