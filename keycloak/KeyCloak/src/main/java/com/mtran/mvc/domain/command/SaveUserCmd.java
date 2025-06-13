package com.mtran.mvc.domain.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveUserCmd {
    private Integer id;
    private String keycloakId;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private LocalDateTime lastChangePassword;
    private boolean passwordSynced;
    private boolean isDeleted;
    private boolean isActive;
}
