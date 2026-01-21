package com.example.schoolgestapp.gestions_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasseDTO {
    private Long id;
    private String name;
    private String code;
    private String academicYear;
    private int maxCapacity;
    private String type;
    
    private Long parentClasseId;
    private String parentClasseName;
    
    private Long responsibleId;
    private String responsibleName;
    
    private Long departmentId;
    private String departmentName;
}
