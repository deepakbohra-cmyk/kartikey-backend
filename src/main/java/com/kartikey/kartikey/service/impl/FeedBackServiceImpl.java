package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;
import com.kartikey.kartikey.entity.FeedBack;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.FeedBackRepository;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.EmailService;
import com.kartikey.kartikey.service.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
