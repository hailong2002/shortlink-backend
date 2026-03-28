package com.example.shortlink.user.domain.mapper;

import com.example.shortlink.auth.domain.dto.UserInfoResponse;
import com.example.shortlink.entites.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserInfoResponse toResponse(User user);
}
