package com.example.schoolgestapp.gestions_academique.dto;

import com.example.schoolgestapp.entity.enums.EvaluationType;
import com.example.schoolgestapp.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private EvaluationType evaluationType;
    private Double score;
    private Double maxScore;
    private Double weight;
    private String feedback;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private Long referenceId;
}
