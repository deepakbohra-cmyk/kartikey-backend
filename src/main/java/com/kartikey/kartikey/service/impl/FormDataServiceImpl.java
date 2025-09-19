package com.kartikey.kartikey.service.impl;


import com.kartikey.kartikey.dto.formdata.FormDataDTO;
import com.kartikey.kartikey.dto.formdata.FormDataFilterDTO;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.FormDataService;
import com.kartikey.kartikey.specification.FormDataSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormDataServiceImpl implements FormDataService {

    private final FormDataRepository formDataRepository;

    private final UserRepository userRepository;

    @Override
    public FormDataDTO saveFormData(FormDataDTO formDataDTO) {

        // Check if the email belongs to L1 team
        Optional<UserEntity> l1User = userRepository.findByEmailAndRole(formDataDTO.getEmail(), UserEntity.Role.L1TEAM);

        if (l1User.isEmpty()) {
            throw new RuntimeException("Email " + formDataDTO.getEmail() + " is not in L1 team");
        }

        FormData formData = FormData.builder()
                .id(formDataDTO.getId())
                .email(formDataDTO.getEmail())
                .workType(formDataDTO.getWorkType())
                .gid(formDataDTO.getGid())
                .decision(formDataDTO.getDecision())
                .createdAt(LocalDateTime.now())
                .build();

        FormData saved = formDataRepository.save(formData);
        return mapToDTO(saved);
    }

    @Override
    public Page<FormDataDTO> getForms(FormDataFilterDTO filter, Pageable pageable) {
        Specification<FormData> spec = FormDataSpecification.withFilters(filter);

        return formDataRepository.findAll(spec, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public FormDataDTO getFormById(Long id) {
        FormData formData = formDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id " + id));
        return mapToDTO(formData);
    }

    private FormDataDTO mapToDTO(FormData formData) {
        return FormDataDTO.builder()
                .id(formData.getId())
                .email(formData.getEmail())
                .workType(formData.getWorkType())
                .gid(formData.getGid())
                .decision(formData.getDecision())
                .createdAt(formData.getCreatedAt())
                .build();
    }

    @Override
    public List<FormDataDTO> searchByGid(String gid) {
        return formDataRepository.findBySimilarGid(gid)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

}