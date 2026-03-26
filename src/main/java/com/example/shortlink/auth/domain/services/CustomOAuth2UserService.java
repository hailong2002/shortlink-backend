package com.example.shortlink.auth.domain.services;

import com.example.shortlink.entites.User;
import com.example.shortlink.entites.UserAuthProvider;
import com.example.shortlink.user.data.repository.UserRepository;
import com.example.shortlink.user_auth_provider.data.repository.UserAuthProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends OidcUserService  {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        log.info("Loading user from OIDC provider: {}", userRequest.getClientRegistration().getRegistrationId());
        OidcUser oidcUser = super.loadUser(userRequest);
        String googleId = oidcUser.getAttribute("sub");
        Optional<UserAuthProvider> existingAuthProvider = userAuthProviderRepository.findByProviderUserId(googleId);
        if (existingAuthProvider.isEmpty()) {
            log.info("User not found in database, creating new user with Google ID: {}", googleId);
            String email = oidcUser.getAttribute("email");
            
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                log.info("User with email {} already exists, merging with Google Provider", email);
            } else {
                user = User.builder()
                        .email(email)
                        .name(email != null ? email.split("@")[0] : "no name")
                        .createdAt(Instant.now())
                        .build();
                userRepository.save(user);
            }
            
            var userAuthProvider = UserAuthProvider.builder()
                    .providerUserId(googleId)
                    .user(user)
                    .provider("google")
                    .createdAt(Instant.now())
                    .build();
            userAuthProviderRepository.save(userAuthProvider);
        }
        log.info("User loaded successfully: {}", oidcUser.getEmail());
        return oidcUser;
    }
}
