package com.example.shortlink.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoResponse {
    private String email;
    private String name;
    private String avatarUrl;
}
