package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.metric.UserMetricsDTO;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.entity.UserMetrics;
import com.kartikey.kartikey.repository.UserMetricsRepository;
import com.kartikey.kartikey.service.UserMetricsService;
import com.kartikey.kartikey.specification.UserMetricSpecification;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMetricsServiceImpl implements UserMetricsService {

    private final UserMetricsRepository userMetricsRepository;

    @Override
    public UserMetrics ensureMetrics(UserEntity user) {
        return userMetricsRepository.findByUser(user)
                .orElseGet(() -> {
                    UserMetrics metrics = UserMetrics.builder()
                            .user(user)
                            .formFilled(0L)
                            .formChecked(0L)
                            .feedbackGiven(0L)
                            .score(0D)
                            .tlScore(0D)
                            .build();
                    return userMetricsRepository.save(metrics);
                });
    }

    private void retryOperation(Runnable operation, String description, String email) {
        int attempts = 0;
        int maxAttempts = 3;
        while (true) {
            try {
                operation.run();
                return; // success
            } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
                attempts++;
                if (attempts >= maxAttempts) {
                    log.error("❌ Failed to {} after {} retries for {}", description, attempts, email);
                    throw ex;
                }
                try {
                    Thread.sleep(50L * attempts); // incremental backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.warn("⚠️ Retrying {} (attempt {} of {}) for {}", description, attempts, maxAttempts, email);
            }
        }
    }

    @Override
    @Transactional
    public void incrementFormFilled(UserEntity user) {
        ensureMetrics(user);
        retryOperation(() -> {
            int rows = userMetricsRepository.incrementFormFilled(user);
            if (rows == 0) {
                throw new RuntimeException("Failed to increment formFilled for " + user.getEmail());
            }
        }, "increment formFilled", user.getEmail());
        log.debug("✅ formFilled incremented for {}", user.getEmail());
    }

    @Override
    @Transactional
    public void incrementFormChecked(UserEntity user) {
        ensureMetrics(user);
        retryOperation(() -> {
            int rows = userMetricsRepository.incrementFormChecked(user);
            if (rows == 0) {
                throw new RuntimeException("Failed to increment formChecked for " + user.getEmail());
            }
        }, "increment formChecked", user.getEmail());
        log.debug("✅ formChecked incremented for {}", user.getEmail());
    }

    @Override
    @Transactional
    public void incrementFeedbackGiven(UserEntity user) {
        ensureMetrics(user);
        retryOperation(() -> {
            int rows = userMetricsRepository.incrementFeedbackGiven(user);
            if (rows == 0) {
                throw new RuntimeException("Failed to increment feedbackGiven for " + user.getEmail());
            }
        }, "increment feedbackGiven", user.getEmail());
        log.debug("✅ feedbackGiven incremented for {}", user.getEmail());
    }

    @Override
    @Transactional
    public void incrementScore(UserEntity user, double delta) {
        ensureMetrics(user);
        retryOperation(() -> {
            int rows = userMetricsRepository.incrementScore(user, delta);
            if (rows == 0) {
                throw new RuntimeException("Failed to increment score for " + user.getEmail());
            }
        }, "increment score", user.getEmail());
        log.debug("✅ score incremented by {} for {}", delta, user.getEmail());
    }

    @Override
    @Transactional
    public void incrementTlScore(UserEntity user, double delta) {
        ensureMetrics(user);
        retryOperation(() -> {
            int rows = userMetricsRepository.incrementTlScore(user, delta);
            if (rows == 0) {
                throw new RuntimeException("Failed to increment TL score for " + user.getEmail());
            }
        }, "increment TL score", user.getEmail());
        log.debug("✅ TL score incremented by {} for {}", delta, user.getEmail());
    }

    @Override
    public Page<UserMetricsDTO> getAllMetrics(String email, UserEntity.Role role, Pageable pageable) {
        Specification<UserMetrics> spec = UserMetricSpecification.withEmail(email);

        // ✅ role filter lagana
        if (role != null) {
            spec = spec.and(UserMetricSpecification.withRole(role));
        }

        return userMetricsRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }


    private UserMetricsDTO mapToDto(UserMetrics userMetrics) {
        if (userMetrics == null || userMetrics.getUser() == null) {
            return null;
        }

        return UserMetricsDTO.builder()
                .id(userMetrics.getId())
                .userEmail(userMetrics.getUser().getEmail())
                .userName(userMetrics.getUser().getUsername())
                .role(userMetrics.getUser().getRole().name())
                .formFilled(userMetrics.getFormFilled())
                .formChecked(userMetrics.getFormChecked())
                .feedbackGiven(userMetrics.getFeedbackGiven())
                .score(userMetrics.getScore())
                .tlScore(userMetrics.getTlScore())
                .updatedAt(userMetrics.getUpdatedAt())
                .build();
    }
}