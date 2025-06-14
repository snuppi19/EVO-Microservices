package com.mtran.mvc.application.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserUpdateParamRequest {
    private boolean enabled;
}
