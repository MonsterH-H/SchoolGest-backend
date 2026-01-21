package com.example.schoolgestapp.gestions_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardDTO {
    private long classesCount;
    private long studentsCount;
    private long assignmentsToGrade;
    private double attendanceRate;
    private String adminNote;
    private List<TeacherTaskDTO> pendingTasks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherTaskDTO {
        private String title;
        private String deadline;
        private String priority; // low, medium, high
        private String type; // grading, preparation, meeting
    }
}
