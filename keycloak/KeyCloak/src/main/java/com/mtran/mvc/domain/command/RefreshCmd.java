package com.mtran.mvc.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class RefreshCmd {
    private String email;
    private String refreshToken;
}
