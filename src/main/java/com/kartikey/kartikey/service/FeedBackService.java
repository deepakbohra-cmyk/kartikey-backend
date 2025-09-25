package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.feedback.FeedBackDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;

import java.util.List;

public interface FeedBackService {
    FeedBackResponseDTO createFeedback(FeedBackRequestDTO requestDTO);
    List<FeedBackDTO> getAllFeedbackForUser(String email);
    FeedBackDTO changeStatus(String status, Long id);
}
