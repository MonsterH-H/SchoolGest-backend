package com.example.schoolgestapp.gestions_presences.dto;

import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour marquer la présence.
 */
@Data
public class AttendanceRequestDTO {
    @NotNull(message = "L'ID de l'etudiant est obligatoire")
    private Long studentId;

    @NotNull(message = "L'ID du planning est obligatoire")
    private Long planningId;

    @NotNull(message = "Le statut est obligatoire")
    private AttendanceStatus status;

    private String notes;
}
