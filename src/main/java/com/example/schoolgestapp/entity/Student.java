package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.Sexe;
import com.example.schoolgestapp.entity.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité etudiant : Profil académique complet d'un élève.
 * L'adresse est héritée de l'utilisateur (User) pour éviter la redondance.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column(unique = true)
    private String studentCode; // Matricule unique

    private LocalDate birthDate; // Date de naissance (LocalDate préférée)

    // L'adresse est supprimée ici car elle est déjà présente dans l'entité User (Héritage logique)

    @Enumerated(EnumType.STRING)
    private Sexe gender;

    private String nationality;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    private Classe classe; // Classe actuelle de l'etudiant

    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.INSCRIT;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate registrationDate; // Date d'inscription effective
}
