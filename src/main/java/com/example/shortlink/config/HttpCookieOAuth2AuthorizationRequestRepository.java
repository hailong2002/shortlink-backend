package com.example.shortlink.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import java.util.Base64;


public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        if (cookie == null) return null;

        return deserialize(cookie.getValue());
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        if (authorizationRequest == null) {
            deleteCookie(request, response, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
            return;
        }

        String value = serialize(authorizationRequest);
        String cookieString = String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d; Secure; SameSite=None", 
                                            OAUTH2_AUTH_REQUEST_COOKIE_NAME, value, COOKIE_EXPIRE_SECONDS);
        response.addHeader("Set-Cookie", cookieString);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        OAuth2AuthorizationRequest requestCookie = loadAuthorizationRequest(request);
        deleteCookie(request, response, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        return requestCookie;
    }

    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        String cookieString = String.format("%s=; Path=/; Max-Age=0", name);
        response.addHeader("Set-Cookie", cookieString);
    }

    private String serialize(OAuth2AuthorizationRequest object) {
        return Base64.getUrlEncoder().encodeToString(
                org.springframework.util.SerializationUtils.serialize(object));
    }

    private OAuth2AuthorizationRequest deserialize(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return (OAuth2AuthorizationRequest) org.springframework.util.SerializationUtils.deserialize(
                    Base64.getUrlDecoder().decode(value)
            );
        } catch (Exception e) {
            System.err.println("Deserialization Error: " + e.getMessage());
            return null;
        }
    }
}
