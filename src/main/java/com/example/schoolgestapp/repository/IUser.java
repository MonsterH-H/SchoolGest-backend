package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IUser extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
