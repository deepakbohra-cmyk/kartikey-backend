package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.QcFormData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QcFormDataRepository extends JpaRepository<QcFormData , Long> {
}
