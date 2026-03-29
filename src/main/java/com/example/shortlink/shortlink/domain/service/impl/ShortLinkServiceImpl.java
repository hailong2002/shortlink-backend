package com.example.shortlink.shortlink.domain.service.impl;


import com.example.shortlink.entites.ShortlinkEntity;
import com.example.shortlink.entites.User;
import com.example.shortlink.infrastructure.exception.UnauthorizedException;
import com.example.shortlink.shortlink.data.repository.ShortlinkEntityRepository;

import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;
import com.example.shortlink.shortlink.domain.service.interfaces.ShortLinkService;
import com.example.shortlink.user.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.shortlink.shared.Constant.SHORT_LINK_URL;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortlinkEntityRepository shortlinkEntityRepository;
    private final UserRepository userRepository;

    @Override
    public ShortLinkResponse getShortLink(String originalUrl, String username) {
        Optional<ShortlinkEntity> optionalShortlinkEntity = shortlinkEntityRepository.findByOriginalUrl(originalUrl);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found");
        }
        String code;
        if (optionalShortlinkEntity.isPresent()) {
            code = optionalShortlinkEntity.get().getCode();
        } else {
            do {
                code = getRandomString();
            } while (isCodeExist(code));
            ShortlinkEntity shortlinkEntity = ShortlinkEntity.builder()
                    .originalUrl(originalUrl)
                    .code(code)
                    .clickCount(0)
                    .user(optionalUser.get())
                    .build();
            shortlinkEntityRepository.save(shortlinkEntity);
        }
        return ShortLinkResponse.builder()
                .isSuccess(true)
                .data(SHORT_LINK_URL + code)
                .build();
    }

    @Override
    public String getOriginalUrl(String code) {
        Optional<ShortlinkEntity> optionalShortlinkEntity = shortlinkEntityRepository.findByCode(code);
        if (optionalShortlinkEntity.isPresent()) {
            return optionalShortlinkEntity.get().getOriginalUrl();
        }
        throw new RuntimeException("Code not found");
    }

    private String getRandomString() {
        int randomLength = ThreadLocalRandom.current().nextInt(5, 11);
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < randomLength; i++) {
            int index = (int) (Math.random()*characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    private boolean isCodeExist(String code) {
        return shortlinkEntityRepository.findByCode(code).isPresent();
    }
}
