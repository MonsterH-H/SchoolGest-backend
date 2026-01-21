package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResultDTO {
    private Long id;
    private Long moduleId;
    private String moduleName;
    private Double average;
    private Integer totalCredits;
    private List<SubjectResultDTO> subjectResults;
}
