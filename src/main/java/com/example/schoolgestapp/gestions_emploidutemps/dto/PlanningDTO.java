package com.example.schoolgestapp.gestions_emploidutemps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningDTO {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private Long classeId;
    private String classeName;
    private Long roomId;
    private String roomName;
    private LocalDate date;
    private Long timeSlotId;
    private String timeSlotLabel; // ex: 08:00 - 10:00
    private com.example.schoolgestapp.entity.enums.CourseType courseType;
    private boolean cancelled;
    private String cancellationReason;
    private boolean rescheduled;
    private Long originalPlanningId;
}
