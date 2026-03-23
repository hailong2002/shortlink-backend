package com.example.shortlink.user.data.repository;

import com.example.shortlink.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}