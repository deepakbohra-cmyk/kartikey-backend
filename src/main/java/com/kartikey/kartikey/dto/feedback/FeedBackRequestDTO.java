package com.kartikey.kartikey.dto.feedback;

import com.kartikey.kartikey.entity.FeedBack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackRequestDTO {
    private Long formId;
    private String agentEmail;
    private String qcEmail;
    private FeedBack.Status status;
    private String decision;
}

