package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.formdata.QcFormDataDTO;
import com.kartikey.kartikey.dto.formdata.QcFormDataFilterDTO;
import com.kartikey.kartikey.entity.QcFormData;
import com.kartikey.kartikey.service.BulkUploadService;
import com.kartikey.kartikey.service.QcFormDataService;
import com.kartikey.kartikey.service.UserMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/super")
@RequiredArgsConstructor
public class SuperAdminController {

    private final BulkUploadService bulkUploadService;
    private final QcFormDataService qcFormDataService;
    private final UserMetricsService userMetricsService;

    @PostMapping("/users")
    public ResponseEntity<String> uploadUsers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bulkUploadService.uploadUsers(file));
    }

    @PostMapping("/forms")
    public ResponseEntity<String> uploadForms(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bulkUploadService.uploadForms(file));
    }

    @GetMapping("/getqcform")
    public ResponseEntity<?> getForms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) QcFormData.WorkType workType,
            @RequestParam(required = false) String gid,
            @RequestParam(required = false) String decision,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (!(size == 50 || size == 100 || size == 200 || size == 500)) {
            size = 50;
        }

        QcFormDataFilterDTO filter = new QcFormDataFilterDTO();
        filter.setEmail(email);
        filter.setWorkType(workType);
        filter.setGid(gid);
        filter.setDecision(decision);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);

        Pageable pageable = PageRequest.of(page, size);
        List<QcFormDataDTO> forms = qcFormDataService.getForms(filter, pageable).getContent();
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/allmetric")
    public ResponseEntity<?> getAllMetrics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String email) {

        if (!(size == 50 || size == 100 || size == 200 || size == 500)) {
            size = 50;
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userMetricsService.getAllMetrics(email, pageable));
    }


}
