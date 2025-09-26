package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.feedback.FeedBackDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;
import com.kartikey.kartikey.entity.FeedBack;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.exception.ResourceNotFoundException;
import com.kartikey.kartikey.repository.FeedBackRepository;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.EmailService;
import com.kartikey.kartikey.service.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackServiceImpl implements FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final UserRepository userRepository;
    private final FormDataRepository formDataRepository;
    private final EmailService emailService;

    @Override
    public FeedBackResponseDTO createFeedback(FeedBackRequestDTO requestDTO) {
        feedBackRepository.findByFormDataId(requestDTO.getFormId())
                .ifPresent(fb -> {
                    throw new RuntimeException("Feedback already exists for this formId: " + requestDTO.getFormId());
                });

        FormData formData = formDataRepository.findById(requestDTO.getFormId())
                .orElseThrow(() -> new RuntimeException("Form not found"));

        UserEntity agent = userRepository.findByEmail(requestDTO.getAgentEmail())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        UserEntity qc = null;
        if (requestDTO.getQcEmail() != null) {
            qc = userRepository.findByEmail(requestDTO.getQcEmail())
                    .orElseThrow(() -> new RuntimeException("QC not found"));
        }

        UserEntity tl = null;
        if (agent.getTlEmail() != null) {
            tl = userRepository.findByEmail(agent.getTlEmail())
                    .orElseThrow(() -> new RuntimeException("Team Lead not found for agent"));
        }

        FeedBack feedback = FeedBack.builder()
                .formData(formData)
                .agent(agent)
                .qcReviewer(qc)
                .teamLead(tl)
                .status(FeedBack.Status.OPEN) // Default OPEN
                .build();

        FeedBack savedFeedback = feedBackRepository.save(feedback);

        if (tl != null) {
            String subject = "ACTION REQUIRED: New Feedback for GID" + formData.getGid() + "- Agent:" + agent.getEmail();

            String body = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                    "<h2 style=\"color: #0056b3;\">New Feedback Alert!</h2>" +
                    "<p>Dear Team Lead,</p>" +
                    "<p>New feedback has been recorded for your agent. Please review the details below:</p>" +
                    "<table border='1' cellpadding='5' cellspacing='0'>" +
                    "<tr><td>Agent Email</td><td>" + agent.getEmail() + "</td></tr>" +
                    "<tr><td>Form GID</td><td>" + formData.getGid() + "</td></tr>" +
                    "<tr><td>Decision</td><td>" + formData.getDecision() + "</td></tr>" +
                    "</table>" +
                    "<p style=\"margin-top: 20px;\"><strong>Action Required:</strong></p>" +
                    "<p>Please ensure <strong>" + agent.getEmail() + "</strong> discusses this feedback with QA <strong>" +
                    (qc != null ? qc.getEmail() : "N/A") + "</strong>.</p>" +
                    "<p>Once the feedback is resolved, click the link below to mark it as closed:</p>" +
                    "<p style=\"text-align: center; margin-top: 25px;\">" +
                    "<a href=\"http://localhost:5173/feedback\" " +
                    "style=\"display: inline-block; padding: 12px 25px; background-color: #28a745; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold;\">Confirm Feedback Closed</a>" +
                    "</p>" +
                    "<p style=\"margin-top: 30px;\">Thank you for your prompt attention.</p>" +
                    "<p>Best regards,<br>The Glimpse Lens System</p>" +
                    "</div>";

            String[] cc = { "ritik.rana@vacobinary.in" };
            emailService.sendFeedbackNotification(tl.getEmail(), cc, subject, body);
        }


        return FeedBackResponseDTO.fromEntity(savedFeedback);
    }

    @Override
    public List<FeedBackDTO> getAllFeedbackForUser(String email) {
        List<FeedBack> feedbackList = feedBackRepository.findAllByUserEmail(email);
        return feedbackList.stream()
                .map(FeedBackServiceImpl::toDTO) // mapper method
                .toList();
    }

    public static FeedBackDTO toDTO(FeedBack feedback) {
        return new FeedBackDTO(
                feedback.getId(),
                feedback.getStatus().name(),
                feedback.getAgent().getEmail(),
                feedback.getQcReviewer() != null ? feedback.getQcReviewer().getEmail() : null,
                feedback.getTeamLead() != null ? feedback.getTeamLead().getEmail() : null,
                feedback.getFormData().getWorkType(),
                feedback.getFormData().getGid(),
                feedback.getFormData().getDecision(),
                feedback.getFormData().getCreatedAt().toLocalDate(),
                feedback.getFormData().getCreatedAt().toLocalTime()
        );
    }

    @Override
    public FeedBackDTO changeStatus(String status, Long id) {
        FeedBack feedback = feedBackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        feedback.setStatus(FeedBack.Status.valueOf(status.toUpperCase())); // ensure valid enum
        FeedBack updated = feedBackRepository.save(feedback);

        return toDTO(updated);
    }
}
