package com.example.schoolgestapp.gestions_admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalStudents;
    private long totalTeachers;
    private long totalClasses;
    private long totalSubjects;
    private double globalAttendanceRate;
    private long reportCardsGenerated;
    private String databaseStatus;
    private LocalDateTime serverTime;
    private String version;
    private String mode;
}
