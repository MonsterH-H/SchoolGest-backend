package com.example.schoolgestapp.gestions_cahier_texte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour renvoyer les détails d'une séance de cours.
 * Évite de renvoyer toute la hiérarchie des entités.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeanceResponseDTO {
    private Long id;
    private LocalDate date;
    private String heureDebut;
    private String heureFin;
    
    // Informations simplifiées de la matière
    private Long matiereId;
    private String matiereNom;
    private String matiereCode;
    
    // Informations simplifiées de l'enseignant
    private Long enseignantId;
    private String enseignantNomComplet;
    
    private String objectifs;
    private String contenuCours;
    private String devoirs;
    private LocalDate dateLimiteDevoir;
    private String fichierCloudUrl;
    private String observations;
    
    // Liens optionnels
    private Long planningId;
    private Long assignmentId;

    // Champs pour compatibilité frontend
    private String title;
    private String description;
    private String classeName;
    private String startTime;
    private String endTime;
}
