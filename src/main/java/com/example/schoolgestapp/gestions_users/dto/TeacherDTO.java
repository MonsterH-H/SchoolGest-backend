package com.example.schoolgestapp.gestions_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String specialties;
    private String office;
    private String departmentName;
    private String cvUrl;
    private LocalDate hireDate;
    private String teacherCode;
}
