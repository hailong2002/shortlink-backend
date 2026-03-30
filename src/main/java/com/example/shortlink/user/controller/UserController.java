package com.example.shortlink.user.controller;

import com.example.shortlink.auth.domain.dto.UserInfoResponse;
import com.example.shortlink.infrastructure.exception.UnauthorizedException;
import com.example.shortlink.user.domain.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.security.Principal;

import static com.example.shortlink.user.api.UserApiPath.USER_API;
import static com.example.shortlink.user.api.UserApiPath.USER_ME;

@RestController
@RequestMapping(USER_API)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping(USER_ME)
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo(Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.error("Unauthorized access attempt to get current user info");
            throw new UnauthorizedException("User have not signed in yet");
        }
        return ResponseEntity.ok(userService.getCurrentUserInfo(principal.getName()));
    }
}
