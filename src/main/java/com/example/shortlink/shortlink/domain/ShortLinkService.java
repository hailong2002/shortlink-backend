package com.example.shortlink.shortlink.domain;

public interface ShortLinkService {
    String getShortLink(String originalUrl);
    String getOriginalUrl(String code);
}
