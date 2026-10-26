package com.example.shortlink.shared.utils;

import com.example.shortlink.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    public static String extractEmailFromCookie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new UnauthorizedException("User have not signed in yet");
        return auth.getName();
    }
}
