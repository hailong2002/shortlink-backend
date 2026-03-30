package com.example.shortlink.shortlink.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkHistoryResponse {
    private String originalUrl;
    private String shortenedUrl;
    private Instant createdDate;
    private int clickCount;
}
