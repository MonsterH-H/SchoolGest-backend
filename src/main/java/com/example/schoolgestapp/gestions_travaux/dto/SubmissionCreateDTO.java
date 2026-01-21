package com.example.schoolgestapp.gestions_travaux.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionCreateDTO {
    @NotNull(message = "L'ID du devoir est obligatoire")
    private Long assignmentId;
    
    @NotNull(message = "L'ID de l'etudiant est obligatoire")
    private Long studentId;
    
    private String submittedFileUrl;
    private String submissionText;
}
