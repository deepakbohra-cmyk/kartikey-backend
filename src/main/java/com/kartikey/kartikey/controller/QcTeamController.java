package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.formdata.FormDataDTO;
import com.kartikey.kartikey.dto.formdata.FormDataFilterDTO;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.service.FormDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/qcteam")
public class QcTeamController {

    @Autowired
    private FormDataService formDataService;

    @GetMapping
    public ResponseEntity<?> getForms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) FormData.WorkType workType,
            @RequestParam(required = false) String gid,
            @RequestParam(required = false) String decision,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (!(size == 50 || size == 100 || size == 200 || size == 500)) {
            size = 50;
        }

        FormDataFilterDTO filter = new FormDataFilterDTO();
        filter.setEmail(email);
        filter.setWorkType(workType);
        filter.setGid(gid);
        filter.setDecision(decision);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);

        Pageable pageable = PageRequest.of(page, size);
        List<FormDataDTO> forms = formDataService.getForms(filter, pageable).getContent();
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/gid")
    public ResponseEntity<List<FormDataDTO>> searchByGid(@RequestParam String gid) {
        return ResponseEntity.ok(formDataService.searchByGid(gid));
    }

}
