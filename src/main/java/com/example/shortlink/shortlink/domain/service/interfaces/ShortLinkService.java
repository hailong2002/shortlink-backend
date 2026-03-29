package com.example.shortlink.shortlink.domain.service.interfaces;

import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;

public interface ShortLinkService {
    ShortLinkResponse getShortLink(String originalUrl, String username);
    String getOriginalUrl(String code);
}
