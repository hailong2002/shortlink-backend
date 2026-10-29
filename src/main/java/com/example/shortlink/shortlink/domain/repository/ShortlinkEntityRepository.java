package com.example.shortlink.shortlink.domain.repository;

import com.example.shortlink.shortlink.domain.entity.ShortlinkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ShortlinkEntityRepository extends JpaRepository<ShortlinkEntity, Long> {

    Optional<ShortlinkEntity> findByOriginalUrl(String originalUrl);
    Optional<ShortlinkEntity> findByCode(String code);
    Page<ShortlinkEntity> findAllByUserId(Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ShortlinkEntity s SET s.clickCount = COALESCE(s.clickCount, 0) + 1 WHERE s.code = :code")
    void incrementClickCount(@Param("code") String code);
}