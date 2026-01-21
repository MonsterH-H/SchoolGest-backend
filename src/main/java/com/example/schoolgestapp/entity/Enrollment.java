package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Enrollment : Gère l'historique académique d'un etudiant.
 * Lie un etudiant à une classe pour une année académique précise.
 */
@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @Column(nullable = false)
    private String academicYear; // ex: 2024-2025

    @Enumerated(EnumType.STRING)
    private StudentStatus status; // INSCRIT, PASSÉ, REDOUBLÉ, etc.

    private Double finalGrade; // Moyenne annuelle finale

    @CreationTimestamp
    private LocalDateTime enrollmentDate;
}
