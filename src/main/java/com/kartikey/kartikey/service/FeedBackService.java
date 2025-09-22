package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;

public interface FeedBackService {
    FeedBackResponseDTO createFeedback(FeedBackRequestDTO requestDTO);
}
