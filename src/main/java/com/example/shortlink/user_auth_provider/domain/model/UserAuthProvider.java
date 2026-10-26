package com.example.shortlink.user_auth_provider.domain.model;

import com.example.shortlink.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_auth_providers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

}