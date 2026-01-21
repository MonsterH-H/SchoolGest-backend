package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité AbsenceAlert : Système d'alerte progressif pour l'assiduité.
 */
@Entity
@Table(name = "absence_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private int absenceCountSnapshot; // Nombre d'absences au moment de l'alerte
    private String alertLevel; // INFO, WARNING, DANGER, DISCIPLINARY
    
    @Column(columnDefinition = "TEXT")
    private String message;

    private boolean readByStudent = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
