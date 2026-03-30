package com.example.shortlink.shortlink.data.repository;

import com.example.shortlink.entites.ShortlinkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortlinkEntityRepository extends JpaRepository<ShortlinkEntity, Long> {

    Optional<ShortlinkEntity> findByOriginalUrl(String originalUrl);
    Optional<ShortlinkEntity> findByCode(String code);
    Page<ShortlinkEntity> findAllByUserId(Long userId, Pageable pageable);
}