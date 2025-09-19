package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.user.LoginRequest;
import com.kartikey.kartikey.dto.user.LoginResponse;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.service.JwtService;
import com.kartikey.kartikey.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserEntity userEntity = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateTokenWithUserDetails(userEntity);

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getRole().name()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String roleStr = request.getOrDefault("role", "L1TEAM");
            String location = request.get("location");

            UserEntity.Role role;
            try {
                role = UserEntity.Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                role = UserEntity.Role.L1TEAM;
            }

            UserEntity userEntity = userService.createUser(username, password, email, role , location);
            String token = jwtService.generateTokenWithUserDetails(userEntity);

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getRole().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtService.extractUsername(token);

                UserEntity userEntity = userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                String newToken = jwtService.generateTokenWithUserDetails(userEntity);

                return ResponseEntity.ok(new LoginResponse(
                        newToken,
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getEmail(),
                        userEntity.getRole().name()
                ));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}