package com.kartikey.kartikey.dto.feedback;

import com.kartikey.kartikey.entity.FeedBack;
import lombok.Data;

@Data
public class FeedBackRequestDTO {
    private Long formId;
    private String agentEmail;
    private String qcEmail;
    private FeedBack.Status status;
}

