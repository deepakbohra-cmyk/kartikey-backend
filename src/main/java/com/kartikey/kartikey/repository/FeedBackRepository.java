package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    Optional<FeedBack> findByFormDataId(Long formId);
}
