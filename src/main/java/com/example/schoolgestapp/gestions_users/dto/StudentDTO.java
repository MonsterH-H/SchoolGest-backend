package com.example.schoolgestapp.gestions_users.dto;

import com.example.schoolgestapp.entity.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Long classeId;
    private String classeName;
    private String studentNumber;
    private LocalDate birthDate;
    private String birthPlace;
    private String bloodGroup;
    private String medicalInfo;
    private String bio;
    private String nationality;
    private StudentStatus status;
}
