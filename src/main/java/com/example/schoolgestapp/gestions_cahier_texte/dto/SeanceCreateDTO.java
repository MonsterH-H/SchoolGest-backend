package com.example.schoolgestapp.gestions_cahier_texte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO pour capturer les données lors de la création d'une séance.
 */
@Data
public class SeanceCreateDTO {
    
    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotNull(message = "L'ID de la classe est obligatoire")
    private Long cahierDeTexteId; // En réalité, c'est l'ID de la classe - le cahier sera récupéré/créé automatiquement

    @NotNull(message = "L'ID de la matière est obligatoire")
    private Long matiereId;

    @NotNull(message = "L'ID de l'enseignant est obligatoire")
    private Long enseignantId;

    private String objectifs;
    private String contenuCours;
    private String devoirs;
    private LocalDate dateLimiteDevoir;
    private String fichierCloudUrl;
    private String observations;
    
    private Long planningId;
    private Long assignmentId;
}
