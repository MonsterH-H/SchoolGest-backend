package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Une séance pédagogique (entrée dans le cahier de texte).
 */
@Entity
@Table(name = "seances_pedagogiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeancePedagogique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "cahier_de_texte_id", nullable = false)
    @JsonIgnoreProperties({"classe", "creeLe"})
    private CahierDeTexte cahierDeTexte;

    @ManyToOne
    @JoinColumn(name = "planning_id")
    @JsonIgnoreProperties({"subject", "classe", "teacher", "timeSlot", "room"})
    private Planning planning; // Lien optionnel avec l'emploi du temps

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonIgnoreProperties({"subject", "teacher", "classe", "submissions", "createdAt"})
    private Assignment assignment; // Lien avec le module Travaux

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"module", "teacher", "semester", "createdAt", "updatedAt"})
    private Subject matiere;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnoreProperties({"user", "department", "hireDate", "specialties", "proPhone", "office", "cvUrl"})
    private Teacher enseignant;

    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(columnDefinition = "TEXT")
    private String contenuCours;

    @Column(columnDefinition = "TEXT")
    private String devoirs;

    private LocalDate dateLimiteDevoir;

    private String fichierCloudUrl; // Lien Cloudinary

    @Column(columnDefinition = "TEXT")
    private String observations;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    private LocalDateTime modifieLe;
}
