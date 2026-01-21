package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstName;
    private String lastName;
    private String phone;
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String avatarUrl;
    private String address;

    // Champs de sécurité (masqués)
    @JsonIgnore
    private int failedLoginAttempts = 0;
    @JsonIgnore
    private LocalDateTime lockedUntil;
    private boolean emailVerified = false;
    @JsonIgnore
    private String verificationToken;

    // --- Sécurité : Tokens ---
    @JsonIgnore
    private String refreshToken;
    @JsonIgnore
    private LocalDateTime refreshTokenExpiry;

    // --- Sécurité : Réinitialisation de mot de passe ---
    @JsonIgnore
    private String resetToken;
    @JsonIgnore
    private LocalDateTime resetTokenExpiry;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
