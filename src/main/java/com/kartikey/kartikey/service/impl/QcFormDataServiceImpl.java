package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.formdata.QcFormDataDTO;
import com.kartikey.kartikey.dto.formdata.QcFormDataFilterDTO;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.QcFormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.QcFormDataRepository;
import com.kartikey.kartikey.repository.UserMetricsRepository;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.QcFormDataService;
import com.kartikey.kartikey.service.UserMetricsService;
import com.kartikey.kartikey.specification.QcFormDataSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QcFormDataServiceImpl implements QcFormDataService {

    private final QcFormDataRepository qcFormDataRepository;
    private final FormDataRepository formDataRepository;
    private final UserRepository userRepository;
    private final UserMetricsService userMetricsService;

    @Override
    @Transactional
    public QcFormDataDTO saveQcFormData(QcFormDataDTO qcFormDataDTO) {
        UserEntity qcUser = userRepository.findByEmailAndRoleIn(
                qcFormDataDTO.getEmail(),
                List.of(UserEntity.Role.QCTEAM, UserEntity.Role.ADMIN)
        ).orElseThrow(() -> new RuntimeException(
                "Email " + qcFormDataDTO.getEmail() + " is not in QC team or ADMIN"));


        FormData formData = formDataRepository.findById(qcFormDataDTO.getFormId())
                .orElseThrow(() -> new RuntimeException("Form not found with id " + qcFormDataDTO.getFormId()));

        formData.setChecked(true);
        formDataRepository.save(formData);

        QcFormData qcFormData = QcFormData.builder()
                .formId(formData)
                .email(qcFormDataDTO.getEmail())
                .workType(qcFormDataDTO.getWorkType())
                .gid(qcFormDataDTO.getGid())
                .decision(qcFormDataDTO.getDecision())
                .createdAt(LocalDateTime.now())
                .build();

        QcFormData saved = qcFormDataRepository.save(qcFormData);

        userMetricsService.incrementFormFilled(qcUser);

        String l1Email = formData.getEmail();
        UserEntity l1User = userRepository.findByEmailAndRole(l1Email, UserEntity.Role.L1TEAM)
                .orElseThrow(() -> new RuntimeException("L1 user not found for email " + l1Email));

        userMetricsService.incrementFormChecked(l1User);
        return mapToQcDTO(saved);
    }


    private QcFormDataDTO mapToQcDTO(QcFormData qcFormData) {
        return QcFormDataDTO.builder()
                .id(qcFormData.getId())
                .formId(qcFormData.getFormId() != null ? qcFormData.getFormId().getId() : null) // ✅ only ID
                .email(qcFormData.getEmail())
                .workType(qcFormData.getWorkType())
                .gid(qcFormData.getGid())
                .decision(qcFormData.getDecision())
                .date(qcFormData.getCreatedAt() != null ? qcFormData.getCreatedAt().toLocalDate() : null)
                .time(qcFormData.getCreatedAt() != null ? qcFormData.getCreatedAt().toLocalTime() : null)
                .build();
    }

    @Override
    public Page<QcFormDataDTO> getForms(QcFormDataFilterDTO filter, Pageable pageable) {
        Specification<QcFormData> spec = QcFormDataSpecification.withFilters(filter);

        Page<QcFormData> page = qcFormDataRepository.findAll(
                spec.and((root, query, cb) -> root.get("email").in(
                        userRepository.findAll().stream()
                                .filter(u -> u.getRole() == UserEntity.Role.QCTEAM || u.getRole() == UserEntity.Role.ADMIN)
                                .map(UserEntity::getEmail)
                                .toList()
                )),
                pageable
        );

        return page.map(this::mapToQcDTO);
    }

    @Override
    public List<QcFormDataDTO> getQcForms(String email) {
        return qcFormDataRepository.findByEmailOrderByCreatedAtDesc(email) // latest first
                .stream()
                .map(this::mapToQcDTO)
                .toList();
    }
}
