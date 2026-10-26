package com.example.shortlink.shortlink.presentation.mapper;

import com.example.shortlink.shortlink.domain.entity.ShortlinkEntity;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import static com.example.shortlink.shared.Constant.SHORT_LINK_URL;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShortLinkMapper {
    @Mapping(target = "shortenedUrl", expression = "java(getShortenedLink(entity))")
    ShortLinkHistoryResponse toHistoryResponse(ShortlinkEntity entity);

    default String getShortenedLink(ShortlinkEntity entity) {
        return SHORT_LINK_URL + entity.getCode();
    }
}
