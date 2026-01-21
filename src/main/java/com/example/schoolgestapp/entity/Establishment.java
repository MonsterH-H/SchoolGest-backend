package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Entité Etablissement : Gère la structure de haut niveau.
 */
@Entity
@Table(name = "establishments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Establishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String code;

    private String address;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;

    // Paramètres académiques (ex: système de notation, règles)
    @Column(columnDefinition = "TEXT")
    private String academicParameters;

    // Années académiques gérées (ex: "2023-2024, 2024-2025")
    private String academicYears;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Department> departments;
}
