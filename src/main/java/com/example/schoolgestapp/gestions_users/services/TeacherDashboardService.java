package com.example.schoolgestapp.gestions_users.services;

import com.example.schoolgestapp.entity.Subject;
import com.example.schoolgestapp.gestions_users.dto.TeacherDashboardDTO;
import com.example.schoolgestapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@lombok.extern.slf4j.Slf4j
public class TeacherDashboardService {

    private final ISubject subjectRepository;
    private final IStudent studentRepository;
    private final ISubmission submissionRepository;
    private final IAttendance attendanceRepository;
    private final IPlanning planningRepository;

    public TeacherDashboardDTO getTeacherStats(Long teacherId) {
        log.info("Calculating dashboard stats for teacher ID: {}", teacherId);
        
        try {
            // 1. Classes assignées
            List<Subject> subjects = subjectRepository.findByTeacherId(teacherId);
            log.debug("Found {} subjects for teacher {}", subjects.size(), teacherId);
            
            Set<Long> classeIds = subjects.stream()
                    .filter(s -> s.getModule() != null && s.getModule().getClasse() != null)
                    .map(s -> s.getModule().getClasse().getId())
                    .collect(Collectors.toSet());
            
            // 2. Étudiants total
            long totalStudents = 0;
            if (!classeIds.isEmpty()) {
                totalStudents = studentRepository.countByClasseIdIn(classeIds);
            }
            log.debug("Found {} classes and {} total students", classeIds.size(), totalStudents);
    
            // 3. Devoirs à corriger
            long pendingAssignments = submissionRepository.countByAssignment_Teacher_IdAndGradeIsNull(teacherId);
            
            // 4. Taux de présence
            long totalAttendance = attendanceRepository.countByTeacherId(teacherId);
            long presentAttendance = attendanceRepository.countPresentByTeacherId(teacherId);
            
            double attendanceRate = 0;
            if (totalAttendance > 0) {
                attendanceRate = (double) presentAttendance / totalAttendance * 100;
            } else {
                attendanceRate = 100.0;
            }

            // 5. Génération de tâches (REAL INFO)
            List<TeacherDashboardDTO.TeacherTaskDTO> tasks = new ArrayList<>();
            
            if (pendingAssignments > 0) {
                tasks.add(TeacherDashboardDTO.TeacherTaskDTO.builder()
                        .title("Corriger les " + pendingAssignments + " devoirs en attente")
                        .deadline("Dès que possible")
                        .priority("high")
                        .type("grading")
                        .build());
            }

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            List<com.example.schoolgestapp.entity.Planning> tomorrowSessions = planningRepository.findByTeacherIdAndDateBetween(teacherId, tomorrow, tomorrow);
            if (!tomorrowSessions.isEmpty()) {
                tasks.add(TeacherDashboardDTO.TeacherTaskDTO.builder()
                        .title("Préparer les " + tomorrowSessions.size() + " séances de demain")
                        .deadline(tomorrow.toString())
                        .priority("medium")
                        .type("preparation")
                        .build());
            }

            // Ajout d'une tâche de réunion si c'est lundi (exemple de logique métier)
            if (LocalDate.now().getDayOfWeek() == java.time.DayOfWeek.MONDAY) {
                tasks.add(TeacherDashboardDTO.TeacherTaskDTO.builder()
                        .title("Réunion pédagogique hebdomadaire")
                        .deadline("Aujourd'hui 16:00")
                        .priority("medium")
                        .type("meeting")
                        .build());
            }
            
            log.info("Stats calculated successfully for teacher {}: classes={}, students={}, pending={}, rate={}", 
                teacherId, classeIds.size(), totalStudents, pendingAssignments, attendanceRate);
    
            return TeacherDashboardDTO.builder()
                    .classesCount(classeIds.size())
                    .studentsCount(totalStudents)
                    .assignmentsToGrade(pendingAssignments)
                    .attendanceRate(Math.round(attendanceRate * 10.0) / 10.0)
                    .adminNote("La saisie des notes du semestre actuel sera clôturée le 15 du mois prochain. Veuillez vérifier la conformité de vos évaluations.")
                    .pendingTasks(tasks)
                    .build();
        } catch (Exception e) {
            log.error("Error calculating teacher stats for ID {}: {}", teacherId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des statistiques : " + e.getMessage());
        }
    }
}
