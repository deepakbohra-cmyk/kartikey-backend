package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    Optional<FeedBack> findByFormDataId(Long formId);
    @Query("SELECT f FROM FeedBack f " +
            "LEFT JOIN FETCH f.agent " +
            "LEFT JOIN FETCH f.qcReviewer " +
            "LEFT JOIN FETCH f.teamLead " +
            "LEFT JOIN FETCH f.formData " +
            "WHERE f.agent.email = :email " +
            "OR f.qcReviewer.email = :email " +
            "OR f.teamLead.email = :email")
    List<FeedBack> findAllByUserEmail(@Param("email") String email);
}
