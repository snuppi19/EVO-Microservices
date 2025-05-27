package com.mtran.mvc.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface ExcelUserService {
    byte[] exportUsersToExcel(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears) throws Exception;
    void exportUsersToExcelAndStore(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears, String owner) throws Exception;
    int importUsersFromExcel(MultipartFile file);
}
