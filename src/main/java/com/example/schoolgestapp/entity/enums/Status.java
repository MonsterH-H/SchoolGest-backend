package com.example.schoolgestapp.entity.enums;

/**
 * Statuts pour le cycle de vie d'une évaluation ou d'une note.
 */
public enum Status {
    DRAFT,          // En cours de création
    PUBLISHED,      // Visible par les etudiants (énoncé/date)
    GRADING,        // Notes en cours de saisie
    VALIDATED,      // Validé par le coordinateur / Archivage définitif
    ARCHIVED        // Historisé
}
