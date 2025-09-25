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
