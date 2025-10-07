package com.kartikey.kartikey.dto.formdata;

import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.QcFormData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QcFormDataDTO {
    private Long id;
    private FormData formId;
    private String email;
    private QcFormData.WorkType workType;
    private String gid;
    private String decision;
    private LocalDate date;
    private LocalTime time;
}
