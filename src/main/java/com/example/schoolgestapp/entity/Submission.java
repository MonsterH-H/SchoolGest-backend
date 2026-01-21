package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Soumission : Travail rendu par un etudiant pour un devoir donné.
 */
@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String submittedFileUrl;
    private String submissionText; // Optionnel (réponse en texte direct)
    
    private LocalDateTime submissionDate;

    // --- Correction & Feedback ---
    private Double grade;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;

    private boolean late = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Vérifie si le rendu est en retard par rapport à la deadline du devoir.
     */
    public void checkIfLate() {
        if (submissionDate == null) submissionDate = LocalDateTime.now();
        this.late = submissionDate.isAfter(assignment.getDeadline());
    }
}
