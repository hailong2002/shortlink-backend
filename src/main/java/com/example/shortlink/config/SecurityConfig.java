package com.example.shortlink.config;


import com.example.shortlink.auth.domain.services.CustomOAuth2UserService;
import com.example.shortlink.auth.domain.services.JwtAuthFilter;
import com.example.shortlink.auth.domain.services.JwtService;
import com.example.shortlink.auth.domain.services.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsProperties corsProperties;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Kích hoạt cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Tắt CSRF nếu ông dùng Token (JWT). Nếu dùng Session thì cân nhắc để lại.
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // API công khai
                        .requestMatchers("/s/{code}").permitAll() // API công khai
                        .anyRequest().authenticated() // Các API khác phải login
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Thiết lập các tham số dựa trên Best Practice
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Cho phép Frontend đọc header Authorization (nếu cần)
        configuration.setAllowCredentials(true); // Quan trọng: Cho phép gửi Cookie hoặc Header Auth
        configuration.setMaxAge(3600L); // Cache kết quả Preflight trong 1 tiếng để tăng hiệu năng

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho toàn bộ API
        return source;
    }
}
