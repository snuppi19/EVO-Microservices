package com.mtran.mvc.presentation.controller;


import com.mtran.mvc.application.service.command.ExcelUserServiceCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/excel/users")
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelUserServiceCmd userServiceCmd;

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate createdDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate createdDateTo,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false, defaultValue = "admin") String owner) throws Exception {

        byte[] excelBytes = userServiceCmd.exportUsersToExcel(username, fullName, createdDateFrom, createdDateTo, experienceYears);

        userServiceCmd.exportUsersToExcelAndStore(username, fullName, createdDateFrom, createdDateTo, experienceYears, owner);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
    @PostMapping("/import")
    public ResponseEntity<?> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        int result = userServiceCmd.importUsersFromExcel(file);
        return ResponseEntity.ok("Đã import thành công " + result+ " người dùng.");
    }

}
