package com.kartikey.kartikey.dto.metric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricsDTO {
    private Long id;
    private String userEmail;
    private String userName;
    private String role;
    private Long formFilled;
    private Long formChecked;
    private Long feedbackGiven;
    private Double score;
    private Double tlScore;
    private LocalDateTime updatedAt;
}
