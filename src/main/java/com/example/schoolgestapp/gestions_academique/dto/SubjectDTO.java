package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String code;
    private String name;
    private Integer hoursCM;
    private Integer hoursTD;
    private Integer hoursTP;
    private Integer credits;
    private Double coefficientCC;
    private Double coefficientExam;
    
    private Long moduleId;
    private String moduleName;
    
    private Long teacherId;
    private String teacherName;
    
    private Long semesterId;
    private String semesterName;
}
