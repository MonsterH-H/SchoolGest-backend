package com.example.schoolgestapp.entity;

import com.example.schoolgestapp.entity.enums.CourseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité Planning : Séance de cours planifiée.
 */
@Entity
@Table(name = "plannings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Salle room;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private CourseType courseType = CourseType.CM;

    private boolean cancelled = false;
    private String cancellationReason;

    private boolean rescheduled = false;
    private Long originalPlanningId; // Lien vers la séance d'origine si reporté

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Helper pour vérifier si la séance est passée.
     */
    public boolean isPassed() {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            return true;
        }
        if (date.isEqual(today)) {
            return timeSlot.getEndTime().isBefore(java.time.LocalTime.now());
        }
        return false;
    }
}
