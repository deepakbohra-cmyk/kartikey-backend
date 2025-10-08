package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.entity.UserMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserMetricsRepository extends JpaRepository<UserMetrics , Long> , JpaSpecificationExecutor<UserMetrics> {

    Optional<UserMetrics> findByUser(UserEntity user);

    @Modifying
    @Transactional
    @Query("UPDATE UserMetrics um SET um.formFilled = um.formFilled + 1 WHERE um.user = :user")
    int incrementFormFilled(@Param("user") UserEntity user);

    @Modifying
    @Transactional
    @Query("UPDATE UserMetrics um SET um.formChecked = um.formChecked + 1 WHERE um.user = :user")
    int incrementFormChecked(@Param("user") UserEntity user);

    @Modifying
    @Transactional
    @Query("UPDATE UserMetrics um SET um.feedbackGiven = um.feedbackGiven + 1 WHERE um.user = :user")
    int incrementFeedbackGiven(@Param("user") UserEntity user);

    @Modifying
    @Transactional
    @Query("UPDATE UserMetrics um SET um.score = um.score + :delta WHERE um.user = :user")
    int incrementScore(@Param("user") UserEntity user, @Param("delta") double delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserMetrics um SET um.tlScore = um.tlScore + :delta WHERE um.user = :user")
    int incrementTlScore(@Param("user") UserEntity user, @Param("delta") double delta);
}
