package com.mtran.mvc.domain.command;

import com.mtran.mvc.application.dto.response.UserResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DeleteCmd {
    private UserResponse user;
}
