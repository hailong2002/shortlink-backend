package com.example.shortlink.shortlink.domain;


import com.example.shortlink.entites.ShortlinkEntity;
import com.example.shortlink.shortlink.data.repository.ShortlinkEntityRepository;

import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.shortlink.shared.Constant.SHORT_LINK_URL;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortlinkEntityRepository shortlinkEntityRepository;

    @Override
    public ShortLinkResponse getShortLink(String originalUrl) {
        Optional<ShortlinkEntity> optionalShortlinkEntity = shortlinkEntityRepository.findByOriginalUrl(originalUrl);
        String code;
        if (optionalShortlinkEntity.isPresent()) {
            code = optionalShortlinkEntity.get().getCode();
        } else {
            ShortlinkEntity shortlinkEntity = new ShortlinkEntity();
            shortlinkEntity.setOriginalUrl(originalUrl);
            do {
                code = getRandomString();
            } while (isCodeExist(code));
            shortlinkEntity.setCode(code);
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
