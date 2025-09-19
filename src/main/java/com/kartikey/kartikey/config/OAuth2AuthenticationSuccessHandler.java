package com.kartikey.kartikey.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kartikey.kartikey.entity.UserEntity;
import com.kartikey.kartikey.service.JwtService;
import com.kartikey.kartikey.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String provider = getProvider(request);
            String providerId = getProviderId(oAuth2User, provider);

            log.info("OAuth2 login attempt - Provider: {}, Email: {}, Name: {}", provider, email, name);

            // Process OAuth2 user (create or update)
            UserEntity userEntity = userService.processOAuth2User(provider, providerId, email, name);

            // Generate JWT token
            String token = jwtService.generateTokenWithUserDetails(userEntity);

            // Return JSON response similar to login endpoint
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            responseBody.put("type", "Bearer");
            responseBody.put("id", userEntity.getId());
            responseBody.put("username", userEntity.getUsername());
            responseBody.put("email", userEntity.getEmail());
            responseBody.put("role", userEntity.getRole().name());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));

            log.info("OAuth2 authentication successful for user: {}", email);

        } catch (Exception e) {
            log.error("OAuth2 authentication failed", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private String getProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("google")) {
            return "google";
        } else if (uri.contains("github")) {
            return "github";
        }
        return "unknown";
    }

    private String getProviderId(OAuth2User oAuth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                return oAuth2User.getAttribute("sub");
            case "github":
                Object id = oAuth2User.getAttribute("id");
                return id != null ? id.toString() : null;
            default:
                Object defaultId = oAuth2User.getAttribute("id");
                return defaultId != null ? defaultId.toString() : oAuth2User.getAttribute("sub");
        }
    }
}