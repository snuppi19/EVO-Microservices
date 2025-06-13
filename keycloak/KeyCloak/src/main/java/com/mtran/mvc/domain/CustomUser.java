package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveCustomUser;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser {
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

    public CustomUser(SaveCustomUser command){
        this.id = command.getId();
        this.username = command.getUsername();
        this.fullName = command.getFullName();
        this.birthDate = command.getBirthDate();
        this.street = command.getStreet();
        this.ward = command.getWard();
        this.district = command.getDistrict();
        this.province = command.getProvince();
        this.experienceYears = command.getExperienceYears();
        this.keycloakId = command.getKeycloakId();
        this.isDeleted = command.isDeleted();
    }
}
