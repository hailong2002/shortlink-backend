package com.example.shortlink.shortlink.domain.mapper;

import com.example.shortlink.entites.ShortlinkEntity;
import com.example.shortlink.shortlink.domain.dto.ShortLinkHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShortLinkMapper {
    ShortLinkHistoryResponse toHistoryResponse(ShortlinkEntity entity);
}
