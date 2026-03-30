package com.example.shortlink.shortlink.domain.service.interfaces;

import com.example.shortlink.shared.PageResponse;
import com.example.shortlink.shortlink.domain.dto.ShortLinkHistoryResponse;
import com.example.shortlink.shortlink.domain.dto.ShortLinkRequest;
import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortLinkService {
    ShortLinkResponse getShortLink(String originalUrl);
    String getOriginalUrl(String code);
    PageResponse<ShortLinkHistoryResponse> getHistory(Pageable pageable);
}
