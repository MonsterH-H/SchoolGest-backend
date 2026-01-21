package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité SubjectResult : détail de notes par matière dans un module du bulletin.
 */
@Entity
@Table(name = "subject_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_result_id", nullable = false)
    private ModuleResult moduleResult;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private Double ccAverage; // Moyenne de contrôle continu
    private Double examGrade; // Note d'examen
    private Double finalAverage; // (CC * coeffCC) + (Exam * coeffExam)
    
    private String teacherAppreciation;
}
