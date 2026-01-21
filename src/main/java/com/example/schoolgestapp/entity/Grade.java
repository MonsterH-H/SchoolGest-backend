package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.EvaluationType;
import com.example.schoolgestapp.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Grade : Représente une note individuelle pour un etudiant.
 * C'est le cœur du module Évaluation & Notes.
 */
@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType; // DEVOIR, EXAMEN, CC, etc.

    private Double score; // La note obtenue (ex: 15.5)
    private Double maxScore = 20.0; // Note maximale possible
    private Double weight = 1.0; // Coefficient de cette note spécifique

    @Column(columnDefinition = "TEXT")
    private String feedback; // Commentaire du prof

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime publishedAt;

    // Lien optionnel vers un devoir ou examen spécifique si besoin
    private Long referenceId; 

    /**
     * Calcule la note pondérée.
     */
    public Double getWeightedScore() {
        if (score == null) return 0.0;
        return (score / maxScore) * 20.0 * weight;
    }
}
