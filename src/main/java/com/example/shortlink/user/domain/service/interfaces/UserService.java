package com.example.shortlink.user.domain.service.interfaces;

import com.example.shortlink.auth.domain.dto.UserInfoResponse;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface UserService {
    UserInfoResponse getCurrentUserInfo(String email);
}
