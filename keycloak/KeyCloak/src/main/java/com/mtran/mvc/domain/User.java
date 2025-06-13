package com.mtran.mvc.domain;


import com.mtran.mvc.domain.command.SaveUserCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User {
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

    public User(SaveUserCmd command) {
        this.id = command.getId();
        this.keycloakId = command.getKeycloakId();
        this.email = command.getEmail();
        this.password = command.getPassword();
        this.name = command.getName();
        this.phoneNumber = command.getPhoneNumber();
        this.lastChangePassword = command.getLastChangePassword();
        this.passwordSynced = command.isPasswordSynced();
        this.isDeleted = command.isDeleted();
        this.isActive = command.isActive();
    }
}
