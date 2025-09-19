package com.kartikey.kartikey.repository;

import com.kartikey.kartikey.entity.FormData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormDataRepository extends JpaRepository<FormData, Long> , JpaSpecificationExecutor<FormData> {

    @Query("SELECT f FROM FormData f WHERE f.gid LIKE CONCAT('%', :gid, '%')")
    List<FormData> findBySimilarGid(@Param("gid") String gid);
}
