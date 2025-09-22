package com.kartikey.kartikey.dto.feedback;

import com.kartikey.kartikey.entity.FeedBack;

public record FeedBackResponseDTO(
        Long id,
        String status,
        String agentEmail,
        String qcEmail,
        String tlEmail,
        Long formId
) {
    public static FeedBackResponseDTO fromEntity(FeedBack fb) {
        return new FeedBackResponseDTO(
                fb.getId(),
                fb.getStatus().name(),
                fb.getAgent().getEmail(),
                fb.getQcReviewer() != null ? fb.getQcReviewer().getEmail() : null,
                fb.getTeamLead() != null ? fb.getTeamLead().getEmail() : null,
                fb.getFormData().getId()
        );
    }
}

