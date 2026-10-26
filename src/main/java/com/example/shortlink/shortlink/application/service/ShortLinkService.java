package com.example.shortlink.shortlink.application.service;


import com.example.shortlink.shortlink.domain.entity.ShortlinkEntity;
import com.example.shortlink.shortlink.presentation.dto.RequestToken;
import com.example.shortlink.user.domain.model.User;
import com.example.shortlink.common.exception.AppException;
import com.example.shortlink.shared.PageResponse;
import com.example.shortlink.shortlink.domain.repository.ShortlinkEntityRepository;

import com.example.shortlink.shortlink.presentation.dto.ShortLinkHistoryResponse;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkResponse;
import com.example.shortlink.shortlink.presentation.mapper.ShortLinkMapper;
import com.example.shortlink.user.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.shortlink.shared.Constant.SHORT_LINK_URL;
import static com.example.shortlink.shared.ErrorCode.*;
import static com.example.shortlink.shared.utils.UrlUtils.isValidUrl;
import static com.example.shortlink.shared.utils.UserUtils.extractEmailFromCookie;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortlinkEntityRepository shortlinkEntityRepository;
    private final UserRepository userRepository;
    private final ShortLinkMapper shortLinkMapper;
    private final ObjectProvider<RedissonClient> redissonClientProvider;
    private final String TOKEN_BUCKET = "token.bucket";

    public ShortLinkResponse getShortLink(String originalUrl) {
        isValidUrl(originalUrl);
        Optional<User> optionalUser = userRepository.findByEmail(extractEmailFromCookie());
        if (optionalUser.isEmpty()) {
            throw new AppException(UNAUTHORIZED);
        }
        checkRateLimit(optionalUser.get().getId());
        String code;
        Optional<ShortlinkEntity> optionalShortlinkEntity = shortlinkEntityRepository.findByOriginalUrl(originalUrl);
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
                    .createdAt(Instant.now())
                    .build();
            shortlinkEntityRepository.save(shortlinkEntity);
            RedissonClient redissonClient = redissonClientProvider.getIfAvailable();
            if (redissonClient != null) {
                RMap<String, String> urlMap = redissonClient.getMap("shortlink.code");
                urlMap.put(code, originalUrl);
            }
        }

        return ShortLinkResponse.builder()
                .isSuccess(true)
                .data(SHORT_LINK_URL + code)
                .build();
    }

    private void checkRateLimit(Long userId) {
        RedissonClient redissonClient = redissonClientProvider.getIfAvailable();
        if (redissonClient == null) {
            return;
        }
        RMap<Long, RequestToken> bucketToken = redissonClient.getMap(TOKEN_BUCKET);
        int maxTokens = 100;
        int refillRate = 3;
        Long currentTime = Instant.now().getEpochSecond();
        bucketToken.compute(userId, (key, requestToken) -> {
            if (requestToken == null) {
                return RequestToken.builder()
                        .lastTime(currentTime)
                        .currentTokens(maxTokens - 1)
                        .build();
            }

            int refillToken =  Math.toIntExact((currentTime - requestToken.getLastTime()) / refillRate);
            int newTokens = requestToken.getCurrentTokens() + refillToken;
            if (newTokens < 1) {
                throw new AppException(OUT_OF_TOKEN_REQUEST);
            }
            Long lastTime = refillToken > 0 ? currentTime : requestToken.getLastTime();
            return RequestToken.builder()
                    .lastTime(lastTime)
                    .currentTokens(Math.min(maxTokens,  newTokens - 1))
                    .build();
        });
    }


    public String getOriginalUrl(String code) {
        RedissonClient redissonClient = redissonClientProvider.getIfAvailable();
        if (redissonClient != null) {
            RMap<String, String> urlMap = redissonClient.getMap("shortlink.code");
            if (urlMap.containsKey(code)) {
                return urlMap.get(code);
            }
        }
        Optional<ShortlinkEntity> optionalShortlinkEntity = shortlinkEntityRepository.findByCode(code);
        if (optionalShortlinkEntity.isPresent()) {
            return optionalShortlinkEntity.get().getOriginalUrl();
        }
        throw new AppException(SHORTENED_CODE_NOT_FOUND);
    }

    public PageResponse<ShortLinkHistoryResponse> getHistory(Pageable pageable) {
        Long userId = getUserId();
        Page<ShortlinkEntity> entities = shortlinkEntityRepository.findAllByUserId(userId, pageable);
        return PageResponse.from(entities.map(shortLinkMapper::toHistoryResponse));
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

    private Long getUserId() {
        String email = extractEmailFromCookie();
        Long userId = userRepository.getUserIdByEmail(email);
        if(userId != null ) return userId;
        throw new AppException(UNAUTHORIZED);
    }

    private boolean isCodeExist(String code) {
        return shortlinkEntityRepository.findByCode(code).isPresent();
    }
}
