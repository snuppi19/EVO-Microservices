package com.mtran.mvc.domain.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveCustomUser {
    private Long id;
    private String username;
    private String fullName;
    private LocalDate birthDate;
    private String street;
    private String ward;
    private String district;
    private String province;
    private Integer experienceYears;
    private String keycloakId;
    private boolean isDeleted = false;
}
