package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.formdata.FormDataDTO;
import com.kartikey.kartikey.dto.formdata.FormDataFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FormDataService {
    FormDataDTO saveFormData(FormDataDTO formDataDTO);
    Page<FormDataDTO> getForms(FormDataFilterDTO filter, Pageable pageable);
    FormDataDTO getFormById(Long id);
    List<FormDataDTO> searchByGid(String gid);
}