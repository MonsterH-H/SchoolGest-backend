package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité Semester : définit les périodes académiques.
 */
@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // ex: Semestre 1

    private String academicYear; // ex: 2024-2025

    private LocalDate startDate;
    private LocalDate endDate;

    // Dates importantes
    private LocalDate examStartDate;
    private LocalDate examEndDate;
    private LocalDate vacationStartDate;
    private LocalDate vacationEndDate;

    private boolean active; // Statut (actif/inactif)
}
