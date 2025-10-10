package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.BulkUploadService;
import com.kartikey.kartikey.service.UserMetricsService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkUploadServiceImpl implements BulkUploadService {

    private final UserRepository userRepository;
    private final FormDataRepository formDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMetricsService userMetricsService;

    @Override
    public String uploadUsers(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<UserEntity> users = new ArrayList<>();
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                String email = cols[2].trim();
                if (email.isEmpty() || userRepository.findByEmail(email).isPresent()) continue;

                UserEntity user = UserEntity.builder()
                        .username(cols[0].trim())
                        .password(passwordEncoder.encode(cols[1].trim()))
                        .email(email)
                        .provider(cols[3].trim())
                        .providerId(cols[4].trim())
                        .location(cols[5].trim())
                        .role(UserEntity.Role.valueOf(cols[6].trim().toUpperCase()))
                        .tlEmail(cols[7].trim())
                        .build();
                users.add(user);
            }

            if (!users.isEmpty()) {
                userRepository.saveAll(users);
                users.forEach(userMetricsService::ensureMetrics);
            }
            return "Successfully uploaded " + users.size() + " users.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadForms(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<FormData> forms = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
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
                        .workType(workType)
                        .gid(getCellValue(row.getCell(2)))
                        .decision(getCellValue(row.getCell(3)))
                        .build();

                forms.add(form);
            }

            if (!forms.isEmpty()) {
                formDataRepository.saveAll(forms);

                // ✅ Increment 'formFilled' metric for each user (by email)
                forms.forEach(f -> {
                    userRepository.findByEmail(f.getEmail())
                            .ifPresent(userMetricsService::incrementFormFilled);
                });
            }

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
            default -> null;
        };
    }
}

