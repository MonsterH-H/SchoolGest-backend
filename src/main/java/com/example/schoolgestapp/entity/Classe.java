package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Entité Classe / Promotion : gère le regroupement d'étudiants.
 */
@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // ex: L1 Informatique
    
    @Column(unique = true)
    private String code;
    
    private String academicYear;
    private int maxCapacity;

    private String type; // ex: "PRINCIPALE", "TD", "TP"

    @ManyToOne
    @JoinColumn(name = "parent_classe_id")
    private Classe parentClasse; // Pour les groupes TD/TP liés à une classe principale

    @OneToMany(mappedBy = "parentClasse", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Classe> subGroups;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private Teacher responsible; // Enseignant responsable de la classe

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
