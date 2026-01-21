package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import com.example.schoolgestapp.entity.enums.JustificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entité Présence : Suivi de l'assiduité par séance (Planning).
 */
@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "planning_id", nullable = false)
    private Planning planning;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status = AttendanceStatus.PRESENT; // PRESENT, ABSENT, RETARD

    private LocalTime arrivalTime; // Pour les retards

    @Column(columnDefinition = "TEXT")
    private String notes;

    // --- Gestion de justificatifs ---
    @Enumerated(EnumType.STRING)
    private JustificationStatus justificationStatus = JustificationStatus.NONE;

    private String justificationReason; // Ex: Médical, Familial...
    private String justificationFileUrl;
    
    @ManyToOne
    @JoinColumn(name = "validated_by_id")
    private User validatedBy; // Admin ou Enseignant qui valide

    private LocalDateTime validatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
