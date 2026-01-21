package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResultDTO {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Double ccAverage;
    private Double examGrade;
    private Double finalAverage;
    private String teacherAppreciation;
}
