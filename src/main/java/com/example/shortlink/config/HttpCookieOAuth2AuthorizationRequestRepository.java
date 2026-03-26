package com.example.shortlink.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Tên file là 'HttpCookie' nhưng thực chất bên trong đã được nâng cấp lên 'In-Memory RAM' 
// để bypass 100% rào cản mất Cookie của Cloudflare/Nginx. 
// Standard Cookie sẽ được bạn nghiên cứu sửa network proxy sau!
//public class HttpCookieOAuth2AuthorizationRequestRepository
//        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
//
//    // RAM Cache dự phòng 1000 slot chống tràn
//    private final Map<String, OAuth2AuthorizationRequest> requests =
//        Collections.synchronizedMap(new LinkedHashMap<String, OAuth2AuthorizationRequest>() {
//            @Override
//            protected boolean removeEldestEntry(Map.Entry<String, OAuth2AuthorizationRequest> eldest) {
//                return size() > 1000;
//            }
//        });
//
//    @Override
//    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
//        String state = request.getParameter("state");
//        if (state == null) return null;
//        return requests.get(state); // Lấy lệnh thông qua chuỗi trên thanh URL
//    }
//
//    @Override
//    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
//                                         HttpServletRequest request,
//                                         HttpServletResponse response) {
//
//        if (authorizationRequest == null) {
//            String state = request.getParameter("state");
//            if (state != null) {
//                requests.remove(state);
//            }
//            return;
//        }
//
//        // Nhét thẳng vào RAM, bỏ qua Proxy Network hoàn toàn
//        requests.put(authorizationRequest.getState(), authorizationRequest);
//    }
//
//    @Override
//    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
//                                                                 HttpServletResponse response) {
//        String state = request.getParameter("state");
//        if (state == null) return null;
//        return requests.remove(state); // Lấy và xóa luôn trong RAM
//    }
//}

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
        Cookie cookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        cookie.setDomain("shortlink.io.vn");
        response.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        OAuth2AuthorizationRequest requestCookie = loadAuthorizationRequest(request);
        deleteCookie(request, response, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        return requestCookie;
    }

    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String serialize(OAuth2AuthorizationRequest object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    private OAuth2AuthorizationRequest deserialize(String value) {
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(value)
        );
    }
}
