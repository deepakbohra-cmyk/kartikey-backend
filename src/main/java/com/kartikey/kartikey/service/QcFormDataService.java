package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.formdata.QcFormDataDTO;
import com.kartikey.kartikey.dto.formdata.QcFormDataFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QcFormDataService {
    QcFormDataDTO saveQcFormData(QcFormDataDTO qcFormDataDTO);
    Page<QcFormDataDTO> getForms(QcFormDataFilterDTO filter, Pageable pageable);
    List<QcFormDataDTO> getQcForms(String email);
}
