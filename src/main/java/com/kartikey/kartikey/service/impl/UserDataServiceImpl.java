package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.dto.user.UserDTO;
import com.kartikey.kartikey.dto.user.UserEntityDTO;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.UserRepository;
import com.kartikey.kartikey.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

}
