package com.mtran.mvc.application.service.command.Impl;

import com.mtran.mvc.application.dto.response.UserImportDTO;
import com.mtran.mvc.application.service.command.ExcelUserServiceCmd;
import com.mtran.mvc.domain.CustomUser;
import com.mtran.mvc.domain.repository.CustomUserDomainRepository;
import com.mtran.mvc.infrastructure.persistance.entity.CustomUserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.CustomUserRepository;
import com.mtran.mvc.infrastructure.support.UserImportValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelUserServiceCmdImpl implements ExcelUserServiceCmd {
    private final CustomUserDomainRepository customUserDomainRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RestTemplate restTemplate;
    private final UserImportValidator userImportValidator;

    @Override
    public byte[] exportUsersToExcel(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears) throws Exception {
        List<CustomUser> users = customUserDomainRepository.findAllBysth(buildSpecification(username, fullName, createdDateFrom, createdDateTo, experienceYears));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create font for header
            Font headerFont = workbook.createFont();
            headerFont.setFontName("Times New Roman");
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font dataFont = workbook.createFont();
            dataFont.setFontName("Times New Roman");

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(dataFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Username", "Họ Tên", "Ngày sinh", "Tên đường", "Xã/Phường", "Huyện", "Tỉnh", "Số năm kinh nghiệm"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowIndex = 1;
            for (CustomUser user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rowIndex - 1);
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getFullName());
                row.createCell(3).setCellValue(user.getBirthDate().format(formatter));
                row.createCell(4).setCellValue(user.getStreet());
                row.createCell(5).setCellValue(user.getWard());
                row.createCell(6).setCellValue(user.getDistrict());
                row.createCell(7).setCellValue(user.getProvince());
                row.createCell(8).setCellValue(user.getExperienceYears());

                // Apply data style
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Override
    public void exportUsersToExcelAndStore(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears, String owner) throws Exception {
        byte[] excelBytes = exportUsersToExcel(username, fullName, createdDateFrom, createdDateTo, experienceYears);

        String url = "http://localhost:8082/storage/public/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String accessToken = getAccessToken();
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(excelBytes) {
            @Override
            public String getFilename() {
                return "users_" + System.currentTimeMillis() + ".xlsx";
            }
        });
        body.add("owner", owner);
        body.add("visibility", "true");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(url, request, String.class);
    }

    @Override
    public int importUsersFromExcel(MultipartFile file) {
        List<CustomUserEntity> validUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                UserImportDTO dto = new UserImportDTO();
                dto.setUsername(getCellValue(row.getCell(1)));
                dto.setFullName(getCellValue(row.getCell(2)));
                dto.setBirthDate(getCellValue(row.getCell(3)));
                dto.setStreet(getCellValue(row.getCell(4)));
                dto.setWard(getCellValue(row.getCell(5)));
                dto.setDistrict(getCellValue(row.getCell(6)));
                dto.setProvince(getCellValue(row.getCell(7)));
                dto.setExperienceYears(getCellValue(row.getCell(8)));

                List<String> rowErrors = userImportValidator.validate(dto, i + 1);
                if (!rowErrors.isEmpty()) {
                    errors.addAll(rowErrors);
                    continue;
                }
                LocalDate birthDate = LocalDate.parse(dto.getBirthDate(), dateFormatter);
                int experience = Integer.parseInt(dto.getExperienceYears());

                CustomUserEntity user = new CustomUserEntity(dto.getUsername(), dto.getFullName(), birthDate,
                        dto.getStreet(), dto.getWard(), dto.getDistrict(), dto.getProvince(), experience);

                validUsers.add(user);
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Import failed with errors:\n" + String.join("\n", errors));
            }

            if (!validUsers.isEmpty()) {
                customUserDomainRepository.saveAll(validUsers);
            }

            return validUsers.size();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel", e);
        }
    }

    private String getCellValue(Cell cell) {
        return cell == null ? "" : cell.toString().trim();
    }


    private String getAccessToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }

    private Specification<CustomUserEntity> buildSpecification(String username, String fullName, LocalDate createdDateFrom, LocalDate createdDateTo, Integer experienceYears) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }
            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%"));
            }
            if (createdDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdDateFrom.atStartOfDay()));
            }
            if (createdDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdDateTo.atTime(23, 59, 59)));
            }
            if (experienceYears != null) {
                predicates.add(cb.equal(root.get("experienceYears"), experienceYears));
            }
            predicates.add(cb.equal(root.get("isDeleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
