package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité Enseignant : Représente le profil académique d'un utilisateur de type Enseignant.
 * Un enseignant est lié à un utilisateur (User) et peut appartenir à un département.
 */
@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column(unique = true)
    private String teacherCode;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department; // Liaison forte avec l'entité Department

    private LocalDate hireDate; // Date de recrutement (LocalDate préférée à java.util.Date)
    
    private String specialties; // Liste de matières ou domaines d'expertise
    
    private String proPhone; // Téléphone professionnel distinct
    
    private String office; // Bureau assigné
    
    private String cvUrl; // Lien vers le CV (Cloudinary)
}
