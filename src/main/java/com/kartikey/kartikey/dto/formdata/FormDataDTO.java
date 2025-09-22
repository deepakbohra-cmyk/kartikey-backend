package com.kartikey.kartikey.dto.formdata;

import com.kartikey.kartikey.entity.FormData;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDataDTO {
    private Long id;
    private String email;
    private FormData.WorkType workType;
    private String gid;
    private String decision;
    private LocalDate date;
    private LocalTime time;
}
