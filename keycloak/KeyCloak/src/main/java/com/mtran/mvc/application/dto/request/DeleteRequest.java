package com.mtran.mvc.application.dto.request;

import com.mtran.mvc.application.dto.response.UserResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class DeleteRequest {
    private UserResponse user;
}
