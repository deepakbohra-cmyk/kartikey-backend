package com.kartikey.kartikey.service;

import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.entity.UserMetrics;

public interface UserMetricsService {

    void incrementFormFilled(UserEntity user);

    void incrementFormChecked(UserEntity user);

    void incrementFeedbackGiven(UserEntity user);

    void incrementScore(UserEntity user, double delta);

    void incrementTlScore(UserEntity user, double delta);

    UserMetrics ensureMetrics(UserEntity user);
}
