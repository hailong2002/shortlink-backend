package com.example.shortlink.user_auth_provider.data.repository;

import com.example.shortlink.entites.UserAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    Optional<UserAuthProvider> findByProviderUserId(String id);
}