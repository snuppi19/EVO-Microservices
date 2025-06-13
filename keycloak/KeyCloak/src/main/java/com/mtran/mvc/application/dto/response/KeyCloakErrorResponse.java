package com.mtran.mvc.application.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class KeyCloakErrorResponse {
    String errorMessage;
}
