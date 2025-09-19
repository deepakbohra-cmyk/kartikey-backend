package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.formdata.FormDataDTO;
import com.kartikey.kartikey.service.FormDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/l1team")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class L1TeamController {

    private final FormDataService formDataService;

    @PostMapping
    public ResponseEntity<FormDataDTO> createForm(@RequestBody FormDataDTO formDataDTO) {
        return ResponseEntity.ok(formDataService.saveFormData(formDataDTO));
    }

}
