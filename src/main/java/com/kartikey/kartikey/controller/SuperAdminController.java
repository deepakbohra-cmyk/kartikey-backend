package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.service.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api/super")
@RequiredArgsConstructor
public class SuperAdminController {

    private final BulkUploadService bulkUploadService;

    @PostMapping("/users")
    public ResponseEntity<String> uploadUsers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bulkUploadService.uploadUsers(file));
    }

    @PostMapping("/forms")
    public ResponseEntity<String> uploadForms(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bulkUploadService.uploadForms(file));
    }
}
