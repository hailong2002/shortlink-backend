package com.example.shortlink.auth.domain.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.shortlink.auth.domain.services.JwtService.generateToken;


@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        if (oauthUser != null) {
            String username = oauthUser.getAttribute("email");
            String token = generateToken(username);
            String redirectUrl = "https://shortlink.io.vn";

            Cookie accessTokenCookie = new Cookie("access_token", token);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(15 * 60);
            accessTokenCookie.setDomain("shortlink.io.vn");

            response.addCookie(accessTokenCookie);
            response.sendRedirect(redirectUrl);
        }


    }
}
