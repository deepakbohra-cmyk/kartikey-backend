package com.kartikey.kartikey.service;

import org.springframework.web.multipart.MultipartFile;

public interface BulkUploadService {
    String uploadUsers(MultipartFile file);
    String uploadForms(MultipartFile file);
}
