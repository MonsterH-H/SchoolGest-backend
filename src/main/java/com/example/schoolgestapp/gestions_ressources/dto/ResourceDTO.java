package com.example.schoolgestapp.gestions_ressources.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String type;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private Long classeId;
    private String classeName;
    private boolean published;
    private LocalDateTime createdAt;
}
