package com.example.schoolgestapp.gestions_presences.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import com.example.schoolgestapp.entity.enums.JustificationStatus;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_presences.dto.AttendanceDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SERVICE : Gestion de l'Assiduité et de la Vie Scolaire
 * 
 * Ce service gère :
 * - Le pointage des présences (Appel) lors d'une séance de cours.
 * - Le workflow des justificatifs (Dépôt par l'etudiant, Validation par Admin).
 * - Le système d'alerte automatique basé sur le cumul des absences.
 * - Les statistiques individuelles d'assiduité.
 */
@Service
@Transactional
public class AttendanceService {

    private final IAttendance attendanceRepository;
    private final IAbsenceAlert alertRepository;
    private final IStudent studentRepository;
    private final IPlanning planningRepository;
    private final IUser userRepository;

    public AttendanceService(IAttendance attendanceRepository, 
                             IAbsenceAlert alertRepository, 
                             IStudent studentRepository,
                             IPlanning planningRepository,
                             IUser userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.alertRepository = alertRepository;
        this.studentRepository = studentRepository;
        this.planningRepository = planningRepository;
        this.userRepository = userRepository;
    }

    private Student resolveStudent(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id requis");
        }

        return studentRepository.findById(id)
                .or(() -> userRepository.findById(id).flatMap(studentRepository::findByUser))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "etudiant introuvable"));
    }

    /**
     * Marque la présence d'un etudiant pour une séance précise.
     * Si l'etudiant est absent, déclenche automatiquement le scan d'alerte.
     */
    public AttendanceDTO markAttendance(Long studentId, Long planningId, AttendanceStatus status, String notes) {
        Student student = resolveStudent(studentId);
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new BusinessException("Séance introuvable"));

        // On récupère ou crée l'enregistrement de présence
        Attendance attendance = attendanceRepository.findByStudentAndPlanning(student, planning)
                .orElse(new Attendance());
        
        attendance.setStudent(student);
        attendance.setPlanning(planning);
        attendance.setStatus(status);
        attendance.setNotes(notes);

        Attendance saved = attendanceRepository.save(attendance);

        // Si l'élève est absent, on vérifie s'il faut envoyer une alerte
        if (status == AttendanceStatus.ABSENT) {
            checkAndGenerateAlerts(student);
        }

        return toDTO(saved);
    }

    @Transactional
    public List<AttendanceDTO> markAttendanceBatch(com.example.schoolgestapp.gestions_presences.dto.BatchAttendanceRequestDTO request) {
        Planning planning = planningRepository.findById(request.getPlanningId())
                .orElseThrow(() -> new BusinessException("Séance introuvable"));

        return request.getAttendances().stream().map(att -> {
            Student student = resolveStudent(att.getStudentId());
            Attendance attendance = attendanceRepository.findByStudentAndPlanning(student, planning)
                    .orElse(new Attendance());

            attendance.setStudent(student);
            attendance.setPlanning(planning);
            attendance.setStatus(att.getStatus());
            attendance.setNotes(att.getNotes());

            Attendance saved = attendanceRepository.save(attendance);

            if (att.getStatus() == AttendanceStatus.ABSENT) {
                checkAndGenerateAlerts(student);
            }

            return toDTO(saved);
        }).collect(Collectors.toList());
    }

    /** Permet à l'etudiant de déposer un justificatif (Lien cloud + Motif). */
    public AttendanceDTO submitJustification(Long attendanceId, String reason, String fileUrl) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new BusinessException("Pointage introuvable"));
        attendance.setJustificationReason(reason);
        attendance.setJustificationFileUrl(fileUrl);
        attendance.setJustificationStatus(JustificationStatus.PENDING);
        return toDTO(attendanceRepository.save(attendance));
    }

    /** Valide ou rejette un justificatif déposé. */
    public AttendanceDTO validateJustification(Long attendanceId, boolean accepted, User validator) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new BusinessException("Pointage introuvable"));
        
        attendance.setJustificationStatus(accepted ? JustificationStatus.ACCEPTED : JustificationStatus.REJECTED);
        attendance.setValidatedBy(validator);
        attendance.setValidatedAt(LocalDateTime.now());
        
        return toDTO(attendanceRepository.save(attendance));
    }

    /**
     * SYSTEME D'ALERTE PROGRESSIF (Vie Scolaire)
     * Analyse l'historique de l'etudiant et génère des notifications système
     * selon des seuils critiques (1, 3, 5, 10 absences non justifiées).
     */
    private void checkAndGenerateAlerts(Student student) {
        long absenceCount = attendanceRepository.countByStudentAndStatusAndJustificationStatus(
                student, AttendanceStatus.ABSENT, JustificationStatus.NONE);

        String level = null;
        String message = null;

        if (absenceCount >= 10) {
            level = "DISCIPLINARY";
            message = "ALERTE DISCIPLINAIRE : 10ème absence non justifiée. Conseil de discipline requis.";
        } else if (absenceCount >= 5) {
            level = "DANGER";
            message = "ALERTE CRITIQUE : 5ème absence non justifiée. Convocation immédiate.";
        } else if (absenceCount >= 3) {
            level = "WARNING";
            message = "ALERTE ORANGE : 3ème absence. Information des parents envoyée.";
        } else if (absenceCount == 1) {
            level = "INFO";
            message = "INFO : Première absence enregistrée dans le système.";
        }

        if (level != null) {
            AbsenceAlert alert = new AbsenceAlert();
            alert.setStudent(student);
            alert.setAbsenceCountSnapshot((int) absenceCount);
            alert.setAlertLevel(level);
            alert.setMessage(message);
            alertRepository.save(alert);
        }
    }

    /** Génère un résumé statistique de l'assiduité d'un etudiant. */
    public Map<String, Object> getStudentStats(Long studentId) {
        Student student = resolveStudent(studentId);
        
        List<Attendance> attendances = attendanceRepository.findByStudent(student);
        
        long total = attendances.size();
        long present = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long retarded = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.RETARD).count();

        return Map.of(
            "totalSessions", total,
            "presenceRate", total > 0 ? (double) present / total * 100 : 0,
            "absences", absent,
            "retards", retarded
        );
    }

    public List<AttendanceDTO> getAttendancesByStudent(Long studentId) {
        Student student = resolveStudent(studentId);
        return attendanceRepository.findByStudent(student).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO toDTO(Attendance a) {
        return AttendanceDTO.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getUser().getFirstName() + " " + a.getStudent().getUser().getLastName())
                .planningId(a.getPlanning().getId())
                .subjectName(a.getPlanning().getSubject() != null ? a.getPlanning().getSubject().getName() : "N/A")
                .date(a.getPlanning().getDate().atStartOfDay()) // Simplification
                .status(a.getStatus())
                .notes(a.getNotes())
                .justificationReason(a.getJustificationReason())
                .justificationFileUrl(a.getJustificationFileUrl())
                .justificationStatus(a.getJustificationStatus())
                .validatedByName(a.getValidatedBy() != null ? a.getValidatedBy().getFirstName() + " " + a.getValidatedBy().getLastName() : null)
                .validatedAt(a.getValidatedAt())
                .build();
    }
}
