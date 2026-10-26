package com.example.shortlink.user.data.repository;

import com.example.shortlink.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(
            value = """
        select id from users where email = :email
        """, nativeQuery = true
    )
    Long getUserIdByEmail(String email);
}