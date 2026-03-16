package com.example.shortlink.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shortlink")
public class ShortlinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "original_url", nullable = false, length = Integer.MAX_VALUE)
    private String originalUrl;

    @Column(name = "code", length = 10)
    private String code;

}