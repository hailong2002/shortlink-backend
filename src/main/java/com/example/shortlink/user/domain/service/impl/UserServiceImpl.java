package com.example.shortlink.user.domain.service.impl;

import com.example.shortlink.auth.domain.dto.UserInfoResponse;
import com.example.shortlink.user.domain.model.User;
import com.example.shortlink.common.exception.UserNotFoundException;
import com.example.shortlink.user.data.repository.UserRepository;
import com.example.shortlink.user.domain.mapper.UserMapper;
import com.example.shortlink.user.domain.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserInfoResponse getCurrentUserInfo(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(userMapper::toResponse).
                orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
