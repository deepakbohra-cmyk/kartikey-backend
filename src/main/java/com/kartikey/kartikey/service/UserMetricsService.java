package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.metric.UserMetricsDTO;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.entity.UserMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserMetricsService {

    void incrementFormFilled(UserEntity user);
    void incrementFormChecked(UserEntity user);
    void incrementFeedbackGiven(UserEntity user);
    void incrementScore(UserEntity user, double delta);
    void incrementTlScore(UserEntity user, double delta);
    UserMetrics ensureMetrics(UserEntity user);

    Page<UserMetricsDTO> getAllMetrics(String email,UserEntity.Role role , Pageable pageable);
}
