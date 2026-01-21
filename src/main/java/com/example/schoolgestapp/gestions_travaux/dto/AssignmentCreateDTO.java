package com.example.schoolgestapp.gestions_travaux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentCreateDTO {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    private String description;
    
    @NotNull(message = "La date limite est obligatoire")
    private LocalDateTime deadline;
    
    @NotNull(message = "L'ID de la classe est obligatoire")
    private Long classeId;
    
    @NotNull(message = "L'ID de la matière est obligatoire")
    private Long subjectId;

    @NotNull(message = "L'ID de l'enseignant est obligatoire")
    private Long teacherId;
    
    private String attachedFileUrl;
}
