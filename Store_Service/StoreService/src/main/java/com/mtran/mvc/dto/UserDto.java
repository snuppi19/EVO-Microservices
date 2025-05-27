package com.mtran.mvc.dto;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
public class UserDto {
    private Integer id;
    private String keycloakId;
    private String email;

}