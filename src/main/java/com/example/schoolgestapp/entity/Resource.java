package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Ressource : Gère le partage de documents et supports de cours.
 */
@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private String fileUrl; // Stockage cloud ou local
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    private Classe classe; // Si null, visible par tous ceux qui ont la matière

    private boolean published = true;
    
    private String version = "1.0";

    @CreationTimestamp
    private LocalDateTime uploadDate;
}
