package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authentication.getName();

        UserEntity userEntity = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", userEntity.getId(),
                "username", userEntity.getUsername(),
                "email", userEntity.getEmail(),
                "role", userEntity.getRole().name(),
                "createdAt", userEntity.getCreatedAt()
        ));
    }
}
