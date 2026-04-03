package com.example.shortlink.auth.domain.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;




@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 login successful, generating JWT token");
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        if (oauthUser != null) {
            log.info("OAuth2 user email redirect ");
            String username = oauthUser.getAttribute("email");
            String token = jwtService.generateToken(username);
            String redirectUrl = "https://shortlink.io.vn";

            response.setHeader("Set-Cookie", "token=" + token + "; Path=/api/; HttpOnly; Secure; SameSite=None; Max-Age=900; Domain=shortlink.io.vn");
            response.sendRedirect(redirectUrl);
        }


    }
}
