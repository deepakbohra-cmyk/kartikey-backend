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
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkUploadServiceImpl implements BulkUploadService {

    private final UserRepository userRepository;
    private final FormDataRepository formDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMetricsService userMetricsService;

    // Allowed roles list
    private static final EnumSet<UserEntity.Role> ALLOWED_ROLES = EnumSet.allOf(UserEntity.Role.class);

    @Override
    public String uploadUsers(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new RuntimeException("File has no name");

        if (filename.endsWith(".csv")) {
            return uploadUsersFromCSV(file);
        } else {
            return uploadUsersFromExcel(file);
        }
    }

    private String uploadUsersFromCSV(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            List<UserEntity> users = new ArrayList<>();
            int rowNum = 0;

            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (rowNum == 1) continue; // skip header

                String[] cols = line.split(",");
                if (cols.length < 5) continue; // not enough columns

                String username = cols[0].trim();
                String email = cols[1].trim();
                String roleStr = cols[2].trim();
                String tlEmail = cols[3].trim();
                String location = cols[4].trim();

                // Skip if mandatory fields missing
                if (username.isEmpty() || email.isEmpty() || roleStr.isEmpty() || location.isEmpty()) {
                    continue;
                }

                // Validate role
                UserEntity.Role role;
                try {
                    role = UserEntity.Role.valueOf(roleStr.toUpperCase());
                    if (!ALLOWED_ROLES.contains(role)) continue;
                } catch (Exception e) {
                    continue;
                }

                // Skip duplicates in DB or batch
                if (userRepository.findByEmail(email).isPresent()) continue;
                if (users.stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail()))) continue;

                UserEntity user = UserEntity.builder()
                        .username(username)
                        .email(email)
                        .role(role)
                        .tlEmail(tlEmail)
                        .location(location)
                        .password(passwordEncoder.encode("vbsllp"))
                        .isActive(true)
                        .build();

                users.add(user);
            }

            if (!users.isEmpty()) {
                userRepository.saveAll(users);
                users.forEach(userMetricsService::ensureMetrics);
            }

            return "Successfully uploaded " + users.size() + " users from CSV.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private String uploadUsersFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<UserEntity> users = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String username = getCellValue(row.getCell(0));
                String email = getCellValue(row.getCell(1));
                String roleStr = getCellValue(row.getCell(2));
                String tlEmail = getCellValue(row.getCell(3));
                String location = getCellValue(row.getCell(4));

                if (isBlank(username) || isBlank(email) || isBlank(roleStr) || isBlank(location)) continue;

                UserEntity.Role role;
                try {
                    role = UserEntity.Role.valueOf(roleStr.toUpperCase());
                    if (!ALLOWED_ROLES.contains(role)) continue;
                } catch (Exception e) {
                    continue;
                }

                if (userRepository.findByEmail(email).isPresent()) continue;
                if (users.stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail()))) continue;

                UserEntity user = UserEntity.builder()
                        .username(username)
                        .email(email)
                        .role(role)
                        .tlEmail(tlEmail)
                        .location(location)
                        .password(passwordEncoder.encode("vbsllp"))
                        .isActive(true)
                        .build();

                users.add(user);
            }

            if (!users.isEmpty()) {
                userRepository.saveAll(users);
                users.forEach(userMetricsService::ensureMetrics);
            }

            return "Successfully uploaded " + users.size() + " users from Excel.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
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

                String email = getCellValue(row.getCell(0));
                String workTypeStr = getCellValue(row.getCell(1));
                String gid = getCellValue(row.getCell(2));
                String decision = getCellValue(row.getCell(3));

                if (email == null || email.isBlank()) continue;

                FormData.WorkType workType = null;
                if (workTypeStr != null && !workTypeStr.isBlank()) {
                    try {
                        workType = FormData.WorkType.valueOf(workTypeStr.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }

                FormData form = FormData.builder()
                        .email(email)
                        .workType(workType)
                        .gid(gid)
                        .decision(decision)
                        .build();

                forms.add(form);
            }

            if (!forms.isEmpty()) {
                formDataRepository.saveAll(forms);
                forms.forEach(f -> userRepository.findByEmail(f.getEmail())
                        .ifPresent(userMetricsService::incrementFormFilled));
            }

            return "Successfully uploaded " + forms.size() + " forms.";

        } catch (IOException e) {
            throw new RuntimeException("Error processing form file: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
