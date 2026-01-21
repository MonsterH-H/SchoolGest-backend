package com.example.schoolgestapp.gestions_travaux.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String attachedFileUrl;
    private String solutionFileUrl;
    private Long classeId;
    private String classeName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
}
