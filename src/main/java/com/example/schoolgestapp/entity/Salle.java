package com.example.schoolgestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Salle : gère les salles de classe, laboratoires et amphithéâtres.
 */
@Entity
@Table(name = "salles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private int capacite;

    /**
     * Type de salle : AMPHI, TP, TD, SALLE_DE_CLASSE
     */
    private String type;

    private boolean projecteur;
    private boolean ordinateurs;
    private boolean actif = true;
}
