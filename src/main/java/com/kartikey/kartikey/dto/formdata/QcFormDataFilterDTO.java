package com.kartikey.kartikey.dto.formdata;

import com.kartikey.kartikey.entity.QcFormData;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class QcFormDataFilterDTO {
    private String email;
    private QcFormData.WorkType workType;
    private String gid;
    private String decision;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
