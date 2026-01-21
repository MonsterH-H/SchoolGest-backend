package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité Bulletin : Représente le relevé de notes officiel d'un etudiant pour un semestre.
 */
@Entity
@Table(name = "report_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    private String academicYear;

    private Double average; // Moyenne générale du semestre
    
    private String appreciation; // Commentaire global du conseil de classe
    
    private String rank; // Rang (ex: 1er / 25)

    @Builder.Default
    private boolean validated = false; // Validé par l'administration

    @OneToMany(mappedBy = "reportCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ModuleResult> moduleResults = new java.util.ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
