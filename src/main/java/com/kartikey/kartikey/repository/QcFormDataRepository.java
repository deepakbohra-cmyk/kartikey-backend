package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.QcFormData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcFormDataRepository extends JpaRepository<QcFormData , Long> , JpaSpecificationExecutor<QcFormData> {
    List<QcFormData> findByEmailOrderByCreatedAtDesc(String email);
}
