package com.example.shortlink.shortlink.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestToken {
    private Long lastTime;
    private Integer currentTokens;
}
