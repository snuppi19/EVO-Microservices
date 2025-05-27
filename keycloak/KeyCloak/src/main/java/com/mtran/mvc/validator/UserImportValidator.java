package com.mtran.mvc.validator;

import com.mtran.mvc.dto.UserImportDTO;
import com.mtran.mvc.repository.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserImportValidator {
    private final CustomUserRepository userRepository;

    public List<String> validate(UserImportDTO user, int rowIndex) {
        List<String> errors = new ArrayList<>();

        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Username is empty");
        } else if (userRepository.existsByUsername(user.getUsername())) {
            errors.add("Row " + rowIndex + ": Username '" + user.getUsername() + "' already exists");
        }

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Full Name is empty");
        }

        if (user.getStreet() == null || user.getStreet().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Street is empty");
        }

        if (user.getWard() == null || user.getWard().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Ward is empty");
        }

        if (user.getDistrict() == null || user.getDistrict().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": District is empty");
        }

        if (user.getProvince() == null || user.getProvince().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Province is empty");
        }

        if (user.getBirthDate() == null || user.getBirthDate().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Birth Date is empty");
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate.parse(user.getBirthDate(), formatter);
            } catch (DateTimeParseException e) {
                errors.add("Row " + rowIndex + ": Invalid Birth Date format (expected: dd/MM/yyyy)");
            }
        }

        if (user.getExperienceYears() == null || user.getExperienceYears().trim().isEmpty()) {
            errors.add("Row " + rowIndex + ": Experience Years is empty");
        } else {
            try {
                int years = Integer.parseInt(user.getExperienceYears());
                if (years < 0) {
                    errors.add("Row " + rowIndex + ": Experience Years must be a non-negative number");
                }
            } catch (NumberFormatException e) {
                errors.add("Row " + rowIndex + ": Experience Years must be a number");
            }
        }

        return errors;
    }
}