package com.example.shortlink.shortlink.domain;

import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;

public interface ShortLinkService {
    ShortLinkResponse getShortLink(String originalUrl);
    String getOriginalUrl(String code);
}
