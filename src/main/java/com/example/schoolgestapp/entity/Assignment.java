package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité Devoir : Travail pédagogique créé par un enseignant.
 */
@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    private Classe classe; // Cibler une classe spécifique

    private LocalDateTime deadline;
    
    private Double maxNote = 20.0;
    
    private String attachedFileUrl; // Énoncé ou ressources
    private String solutionFileUrl; // Correction (visible après deadline)

    private boolean published = false;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Submission> submissions;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
