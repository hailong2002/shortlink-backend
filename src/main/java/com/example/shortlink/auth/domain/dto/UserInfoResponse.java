package com.example.shortlink.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String email;
    private String name;
    private String avatarUrl;
}
