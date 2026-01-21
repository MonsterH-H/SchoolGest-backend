package com.example.schoolgestapp.gestions_emploidutemps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String label;
    private Integer position;
}
