package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.user.ChangePasswordRequest;
import com.kartikey.kartikey.dto.user.UserDTO;
import com.kartikey.kartikey.dto.user.UserEntityDTO;
import com.kartikey.kartikey.entity.FormData;
import com.kartikey.kartikey.entity.QcFormData;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.entity.UserMetrics;
import com.kartikey.kartikey.repository.*;
import com.kartikey.kartikey.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMetricsRepository userMetricsRepository;
    private final FormDataRepository formDataRepository;
    private final FeedBackRepository feedBackRepository;
    private final QcFormDataRepository qcFormDataRepository;

    @Override
    public List<UserDTO> getAllUser() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getTlEmail(),
                        user.getLocation()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO addUser(UserEntityDTO userEntityDTO) {
        if (userRepository.existsByEmail(userEntityDTO.getEmail())) {
            throw new RuntimeException("User with email " + userEntityDTO.getEmail() + " already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(userEntityDTO.getUsername())
                .email(userEntityDTO.getEmail())
                .password(passwordEncoder.encode(userEntityDTO.getPassword()))
                .role(UserEntity.Role.valueOf(userEntityDTO.getRole()))
                .tlEmail(userEntityDTO.getTlEmail())
                .location(userEntityDTO.getLocation())
                .build();

        UserEntity saved = userRepository.save(user);

        UserMetrics userMetrics = UserMetrics.builder()
                .user(saved)
                .build();
        userMetricsRepository.save(userMetrics);

        return new UserDTO(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getTlEmail(),
                saved.getLocation()
        );
    }

    @Override
    public UserDTO updateUser(Long id, UserEntityDTO userEntityDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        // only update fields if they are present (not null / not blank)
        if (userEntityDTO.getUsername() != null && !userEntityDTO.getUsername().isBlank()) {
            user.setUsername(userEntityDTO.getUsername());
        }

        if (userEntityDTO.getEmail() != null && !userEntityDTO.getEmail().isBlank()) {
            if (!user.getEmail().equals(userEntityDTO.getEmail()) &&
                    userRepository.existsByEmail(userEntityDTO.getEmail())) {
                throw new RuntimeException("User with email " + userEntityDTO.getEmail() + " already exists");
            }
            user.setEmail(userEntityDTO.getEmail());
        }

        if (userEntityDTO.getPassword() != null && !userEntityDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userEntityDTO.getPassword()));
        }

        if (userEntityDTO.getRole() != null) {
            user.setRole(UserEntity.Role.valueOf(userEntityDTO.getRole()));
        }

        if (userEntityDTO.getTlEmail() != null) {
            user.setTlEmail(userEntityDTO.getTlEmail());
        }

        if (userEntityDTO.getLocation() != null) {
            user.setLocation(userEntityDTO.getLocation());
        }

        UserEntity updated = userRepository.save(user);

        return new UserDTO(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getRole().name(),
                updated.getTlEmail(),
                updated.getLocation()
        );
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
        // Find user
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        try {
            // Delete UserMetrics
            userMetricsRepository.deleteByUser(user);

            feedBackRepository.deleteByAgentOrQcReviewerOrTeamLead(user, user, user);

            // Delete FormData created by user (if email matches)
            List<FormData> forms = formDataRepository.findByEmail(user.getEmail());
            for (FormData form : forms) {
                // Cascade will delete associated FeedBack automatically if orphanRemoval = true
                formDataRepository.delete(form);
            }

            List<QcFormData> qcForms = qcFormDataRepository.findByEmail(user.getEmail());
            for (QcFormData qcForm : qcForms) {
                qcFormDataRepository.delete(qcForm);
            }
            userRepository.delete(user);

            return "User deleted successfully";

        } catch (Exception e) {
            throw new RuntimeException("Cannot delete user because some related data exists: " + e.getMessage());
        }
    }


    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getTlEmail(),
                        user.getLocation()
                ))
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email " + request.getEmail()));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));

        String defaultPassword = "#*pass12*#";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
    }

}
