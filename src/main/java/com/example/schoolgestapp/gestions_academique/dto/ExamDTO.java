package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private Long id;
    private String name;
    private Long subjectId;
    private String subjectName;
    private Date examDate;
    private String examType;
    private double coefficient;
    private String room;
    private int durationMinutes;
    private String instructions;
}
