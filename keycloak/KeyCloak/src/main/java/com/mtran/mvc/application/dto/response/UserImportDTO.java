package com.mtran.mvc.application.dto.response;

import lombok.Data;

@Data
public class UserImportDTO {
    private int stt;
    private String username;
    private String fullName;
    private String birthDate;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String experienceYears;
}