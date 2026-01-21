package com.example.schoolgestapp.gestions_presences.dto;

import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import com.example.schoolgestapp.entity.enums.JustificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long planningId;
    private String subjectName;
    private LocalDateTime date;
    private AttendanceStatus status;
    private String notes;
    private String justificationReason;
    private String justificationFileUrl;
    private JustificationStatus justificationStatus;
    private String validatedByName;
    private LocalDateTime validatedAt;
}
