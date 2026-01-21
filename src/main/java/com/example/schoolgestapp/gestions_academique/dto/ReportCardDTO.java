package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long semesterId;
    private String semesterName;
    private String academicYear;
    private Double average;
    private Integer rank;
    private String appreciation;
    private boolean validated;
    private LocalDateTime createdAt;
    private List<ModuleResultDTO> moduleResults;
}
