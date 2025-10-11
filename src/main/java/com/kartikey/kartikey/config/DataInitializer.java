package com.kartikey.kartikey.config;

import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "superadmin@gmail.com";

        if (userRepository.findByEmail(email).isEmpty()) {
            UserEntity superAdmin = UserEntity.builder()
                    .username("Super Admin")
                    .email(email)
                    .password(passwordEncoder.encode("Super@123"))
                    .role(UserEntity.Role.ADMIN)
                    .location("Default Location")
                    .provider("local")
                    .build();

            userRepository.save(superAdmin);
            System.out.println("✅ Super admin created: " + email + " / Super@123");
        } else {
            System.out.println("ℹ️ Super admin already exists: " + email);
        }
    }
}
