package com.example.shortlink.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Tên file là 'HttpCookie' nhưng thực chất bên trong đã được nâng cấp lên 'In-Memory RAM' 
// để bypass 100% rào cản mất Cookie của Cloudflare/Nginx. 
// Standard Cookie sẽ được bạn nghiên cứu sửa network proxy sau!
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // RAM Cache dự phòng 1000 slot chống tràn
    private final Map<String, OAuth2AuthorizationRequest> requests =
        Collections.synchronizedMap(new LinkedHashMap<String, OAuth2AuthorizationRequest>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, OAuth2AuthorizationRequest> eldest) {
                return size() > 1000;
            }
        });

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String state = request.getParameter("state");
        if (state == null) return null;
        return requests.get(state); // Lấy lệnh thông qua chuỗi trên thanh URL
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        if (authorizationRequest == null) {
            String state = request.getParameter("state");
            if (state != null) {
                requests.remove(state);
            }
            return;
        }

        // Nhét thẳng vào RAM, bỏ qua Proxy Network hoàn toàn
        requests.put(authorizationRequest.getState(), authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String state = request.getParameter("state");
        if (state == null) return null;
        return requests.remove(state); // Lấy và xóa luôn trong RAM
    }
}
