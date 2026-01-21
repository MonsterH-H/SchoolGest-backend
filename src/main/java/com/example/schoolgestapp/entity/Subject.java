package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité Subject (Matière / UE) : une discipline ou cours spécifique.
 */
@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private Integer hoursCM; // Heures Cours Magistraux
    private Integer hoursTD; // Heures Travaux Dirigés
    private Integer hoursTP; // Heures Travaux Pratiques

    private Integer credits; // Crédits ECTS de la matière

    private Double coefficientCC = 0.4; // Poids du Contrôle Continu (ex: 0.4 pour 40%)
    private Double coefficientExam = 0.6; // Poids de l'Examen (ex: 0.6 pour 60%)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String prerequisite;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module; // Le module auquel appartient cette matière

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // Enseignant titulaire

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
