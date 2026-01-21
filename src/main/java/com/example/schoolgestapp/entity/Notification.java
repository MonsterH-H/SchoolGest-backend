package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Notification : Alertes système et notifications utilisateur.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private String type; // INFO, SUCCESS, WARNING, DANGER, SYSTEM
    
    private String actionUrl; // Lien vers l'action concernée

    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
