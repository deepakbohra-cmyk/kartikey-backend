package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkUploadServiceImpl implements BulkUploadService {

    private final UserRepository userRepository;
    private final FormDataRepository formDataRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String uploadUsers(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<UserEntity> users = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String email = getCellValue(row.getCell(2));
                if (email == null) continue;

                // check if email already exists in DB
                if (userRepository.findByEmail(email).isPresent()) {
                    // skip duplicates
                    continue;
                }

                // also check within the current batch (Excel might have duplicate emails)
                boolean alreadyInBatch = users.stream()
                        .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
                if (alreadyInBatch) continue;

                UserEntity user = UserEntity.builder()
                        .username(getCellValue(row.getCell(0)))
                        .password(passwordEncoder.encode(getCellValue(row.getCell(1))))
                        .email(email)
                        .provider(getCellValue(row.getCell(3)))
                        .providerId(getCellValue(row.getCell(4)))
                        .location(getCellValue(row.getCell(5)))
                        .role(UserEntity.Role.valueOf(getCellValue(row.getCell(6)).toUpperCase()))
                        .tlEmail(getCellValue(row.getCell(7)))
                        .build();

                users.add(user);
            }

            if (!users.isEmpty()) {
                userRepository.saveAll(users);
            }


            if (!users.isEmpty()) {
                userRepository.saveAll(users);
            }
            return "Successfully uploaded " + users.size() + " users.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadForms(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<FormData> forms = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String workTypeStr = getCellValue(row.getCell(1));
                FormData.WorkType workType = null;
                if (workTypeStr != null && !workTypeStr.isBlank()) {
                    try {
                        workType = FormData.WorkType.valueOf(workTypeStr.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid workType value at row " + (i + 1) + ": " + workTypeStr);
                    }
                }

                FormData form = FormData.builder()
                        .email(getCellValue(row.getCell(0)))
                        .workType(workType)  // safe assignment
                        .gid(getCellValue(row.getCell(2)))
                        .decision(getCellValue(row.getCell(3)))
                        .build();

                forms.add(form);
            }

            formDataRepository.saveAll(forms);
            return "Successfully uploaded " + forms.size() + " forms.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> null;
            default -> null; // handles FORMULA, ERROR, etc.
        };
    }
}
