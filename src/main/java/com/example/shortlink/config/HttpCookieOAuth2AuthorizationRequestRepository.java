package com.example.shortlink.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
        // Best Practice Production: SameSite=Lax chặn tấn công CSRF nhưng vẫn cho phép Top-Level-Navigation từ Google về.
        // Hạn chế lỗi chặn Third-Party Cookies của Tab Ẩn danh Chrome.
        String cookieString = String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d; Secure; SameSite=Lax", 
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

    @SuppressWarnings("deprecation")
    private String serialize(OAuth2AuthorizationRequest object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(org.springframework.util.SerializationUtils.serialize(object));
            gzip.finish();
            // Xóa padding '=' để Cookie nhìn gọn hơn và không bị lỗi parser
            return Base64.getUrlEncoder().withoutPadding().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    private OAuth2AuthorizationRequest deserialize(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(value);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                 GZIPInputStream gzip = new GZIPInputStream(bais);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = gzip.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                
                return (OAuth2AuthorizationRequest) org.springframework.util.SerializationUtils.deserialize(buffer.toByteArray());
            }
        } catch (Exception e) {
            System.err.println("Deserialization Error: " + e.getMessage());
            return null;
        }
    }
}
