package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.QcFormData;
import com.kartikey.kartikey.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QcFormDataRepository extends JpaRepository<QcFormData , Long> , JpaSpecificationExecutor<QcFormData> {
    List<QcFormData> findByEmailOrderByCreatedAtDesc(String email);
    List<QcFormData> findByEmail(String email);
    List<QcFormData> findByCreatedAtBefore(LocalDateTime dateTime);
    boolean existsByFormId(FormData form);
}
