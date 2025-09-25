package com.kartikey.kartikey.dto.feedback;

import com.kartikey.kartikey.entity.FormData;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FeedBackDTO {
    Long id;
    String status;
    String email;
    String qcEmail;
    String tlEmail;
    FormData.WorkType workType;
    String gid;
    String decision;
    LocalDate date;
    LocalTime time;

    public FeedBackDTO(Long id, String status, String email, String qcEmail, String tlEmail,
                       FormData.WorkType workType, String gid, String decision,
                       LocalDate date, LocalTime time) {
        this.id = id;
        this.status = status;
        this.email = email;
        this.qcEmail = qcEmail;
        this.tlEmail = tlEmail;
        this.workType = workType;
        this.gid = gid;
        this.decision = decision;
        this.date = date;
        this.time = time;
    }
}
