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
public class ModuleDTO {
    private Long id;
    private String name;
    private Integer credits;
    
    private Long classeId;
    private String classeName;
    
    private Long semesterId;
    private String semesterName;
    
    private List<SubjectDTO> subjects;
}
