package com.example.schoolgestapp.gestions_presences.dto;

import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import lombok.Data;

import java.util.List;

@Data
public class BatchAttendanceRequestDTO {
    private Long planningId;
    private List<StudentAttendanceDTO> attendances;

    @Data
    public static class StudentAttendanceDTO {
        private Long studentId;
        private AttendanceStatus status;
        private String notes;
    }
}
