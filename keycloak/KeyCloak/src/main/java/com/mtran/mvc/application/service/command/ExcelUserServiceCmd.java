package com.mtran.mvc.application.service.command;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface ExcelUserServiceCmd {
    byte[] exportUsersToExcel(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears) throws Exception;
    void exportUsersToExcelAndStore(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears, String owner) throws Exception;
    int importUsersFromExcel(MultipartFile file);
}
