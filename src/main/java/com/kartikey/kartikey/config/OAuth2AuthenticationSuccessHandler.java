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

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        UserEntity userEntity = userService.processOAuth2User("google", "googleId", email, name);
        String token = jwtService.generateTokenWithUserDetails(userEntity);

        // ✅ Redirect directly to frontend with token
        String redirectUrl = String.format(
                "http://localhost:5173/oauth2/redirect"
                        + "?token=" + token
                        + "&email=" + userEntity.getEmail()
                        + "&role=" + userEntity.getRole().name()
        );
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
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