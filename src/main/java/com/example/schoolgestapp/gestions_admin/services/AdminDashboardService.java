package com.example.schoolgestapp.gestions_admin.services;

import com.example.schoolgestapp.gestions_admin.dto.DashboardStatsDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service pour le tableau de bord d'administration globale.
 */
@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final IStudent studentRepository;
    private final ITeacher teacherRepository;
    private final IClasse classeRepository;
    private final ISubject subjectRepository;
    private final IAttendance attendanceRepository;
    private final IReportCard reportCardRepository;

    public AdminDashboardService(IStudent studentRepository,
                                 ITeacher teacherRepository,
                                 IClasse classeRepository,
                                 ISubject subjectRepository,
                                 IAttendance attendanceRepository,
                                 IReportCard reportCardRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classeRepository = classeRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.reportCardRepository = reportCardRepository;
    }

    public DashboardStatsDTO getGlobalDashboardStats() {
        long studentCount = studentRepository.count();
        long teacherCount = teacherRepository.count();
        long classeCount = classeRepository.count();
        long subjectCount = subjectRepository.count();
        long reportCardsCount = reportCardRepository.count();

        // Calculer le taux de présence global
        long totalAttendances = attendanceRepository.count();
        double attendanceRate = 0.0;
        if (totalAttendances > 0) {
            long presentCount = attendanceRepository.findAll().stream()
                    .filter(a -> a.getStatus().name().equals("PRESENT")).count();
            attendanceRate = (double) presentCount / totalAttendances * 100;
        }

        DashboardStatsDTO.DashboardStatsDTOBuilder builder = DashboardStatsDTO.builder()
                .totalStudents(studentCount)
                .totalTeachers(teacherCount)
                .totalClasses(classeCount)
                .totalSubjects(subjectCount)
                .globalAttendanceRate(attendanceRate)
                .reportCardsGenerated(reportCardsCount)
                .databaseStatus("CONNECTED")
                .serverTime(LocalDateTime.now())
                .version("2.0-STABLE")
                .mode("PRODUCTION");

        return builder.build();
    }
}
