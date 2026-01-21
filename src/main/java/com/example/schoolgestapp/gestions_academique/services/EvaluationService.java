package com.example.schoolgestapp.gestions_academique.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.entity.enums.EvaluationType;
import com.example.schoolgestapp.entity.enums.Status;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_academique.dto.ExamDTO;
import com.example.schoolgestapp.gestions_academique.dto.GradeDTO;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de gestion des évaluations - OPTIMISÉ.
 */
@Service
@Transactional
public class EvaluationService {

    private final IGrade gradeRepository;
    private final IStudent studentRepository;
    private final ISubject subjectRepository;
    private final IAssignment assignmentRepository;
    private final IExam examRepository;
    private final ITeacher teacherRepository;

    public EvaluationService(IGrade gradeRepository, 
                             IStudent studentRepository, 
                             ISubject subjectRepository,
                             IAssignment assignmentRepository,
                             IExam examRepository,
                             ITeacher teacherRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
        this.examRepository = examRepository;
        this.teacherRepository = teacherRepository;
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public ExamDTO createExam(Exam exam) {
        return toExamDTO(examRepository.save(exam));
    }

    public GradeDTO saveGrade(GradeDTO dto) {
        return toGradeDTO(gradeRepository.save(toEntity(dto)));
    }

    public List<GradeDTO> saveGradesInBatch(List<GradeDTO> dtos) {
        List<Grade> entities = dtos.stream().map(this::toEntity).collect(Collectors.toList());
        return gradeRepository.saveAll(entities).stream().map(this::toGradeDTO).collect(Collectors.toList());
    }

    public void publishGradesForSubject(Long subjectId, EvaluationType type) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        
        // Optimisation SQL
        List<Grade> grades = gradeRepository.findBySubjectAndEvaluationType(subject, type);
        
        grades.forEach(g -> {
            g.setStatus(Status.PUBLISHED);
            g.setPublishedAt(LocalDateTime.now());
        });
        gradeRepository.saveAll(grades);
    }

    public void validateGradesForSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        
        List<Grade> grades = gradeRepository.findBySubject(subject);
        grades.forEach(g -> g.setStatus(Status.VALIDATED));
        gradeRepository.saveAll(grades);
    }

    public Double calculateSubjectAverage(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        
        List<Grade> grades = gradeRepository.findByStudentAndSubject(student, subject);

        // Moyenne CC (Notes hors examens finaux/partiels)
        List<Grade> ccGrades = grades.stream()
                .filter(g -> g.getEvaluationType() != EvaluationType.EXAMEN_FINAL && g.getEvaluationType() != EvaluationType.EXAMEN_PARTIEL)
                .toList();
        
        double ccSum = ccGrades.stream().mapToDouble(Grade::getWeightedScore).sum();
        double ccWeightSum = ccGrades.stream().mapToDouble(Grade::getWeight).sum();
        double ccAvg = ccWeightSum > 0 ? (ccSum / ccWeightSum) : 0.0;

        // Moyenne Examen
        List<Grade> examGrades = grades.stream()
                .filter(g -> g.getEvaluationType() == EvaluationType.EXAMEN_FINAL || g.getEvaluationType() == EvaluationType.EXAMEN_PARTIEL)
                .toList();

        double examSum = examGrades.stream().mapToDouble(Grade::getWeightedScore).sum();
        double examWeightSum = examGrades.stream().mapToDouble(Grade::getWeight).sum();
        double examAvg = examWeightSum > 0 ? (examSum / examWeightSum) : 0.0;

        // La note finale pondérée de la matière
        return (ccAvg * subject.getCoefficientCC()) + (examAvg * subject.getCoefficientExam());
    }

    public Map<String, Double> getSubjectStats(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BusinessException("Matière introuvable"));
        
        List<Grade> grades = gradeRepository.findBySubject(subject);
        if (grades.isEmpty()) return Map.of("average", 0.0, "max", 0.0, "min", 0.0);

        double avg = grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
        double max = grades.stream().mapToDouble(Grade::getScore).max().orElse(0.0);
        double min = grades.stream().mapToDouble(Grade::getScore).min().orElse(0.0);

        return Map.of("average", avg, "max", max, "min", min);
    }

    public List<GradeDTO> getGradesByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));
        return gradeRepository.findByStudent(student).stream().map(this::toGradeDTO).collect(Collectors.toList());
    }

    /* ===================== mapping methode ================= */

    private GradeDTO toGradeDTO(Grade g) {
        if (g == null) return null;
        GradeDTO.GradeDTOBuilder builder = GradeDTO.builder()
                .id(g.getId())
                .evaluationType(g.getEvaluationType())
                .score(g.getScore())
                .maxScore(g.getMaxScore())
                .weight(g.getWeight())
                .feedback(g.getFeedback())
                .status(g.getStatus())
                .createdAt(g.getCreatedAt())
                .publishedAt(g.getPublishedAt())
                .referenceId(g.getReferenceId());

        if (g.getStudent() != null) {
            builder.studentId(g.getStudent().getId());
            builder.studentName(g.getStudent().getUser().getFirstName() + " " + g.getStudent().getUser().getLastName());
        }

        if (g.getSubject() != null) {
            builder.subjectId(g.getSubject().getId());
            builder.subjectName(g.getSubject().getName());
        }

        if (g.getTeacher() != null) {
            builder.teacherId(g.getTeacher().getId());
            builder.teacherName(g.getTeacher().getUser().getFirstName() + " " + g.getTeacher().getUser().getLastName());
        }

        return builder.build();
    }

    private Grade toEntity(GradeDTO dto) {
        if (dto == null) return null;
        Grade g = new Grade();
        g.setId(dto.getId());
        g.setEvaluationType(dto.getEvaluationType());
        g.setScore(dto.getScore());
        g.setMaxScore(dto.getMaxScore() != null ? dto.getMaxScore() : 20.0);
        g.setWeight(dto.getWeight() != null ? dto.getWeight() : 1.0);
        g.setFeedback(dto.getFeedback());
        g.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.DRAFT);
        g.setCreatedAt(dto.getCreatedAt());
        g.setPublishedAt(dto.getPublishedAt());
        g.setReferenceId(dto.getReferenceId());

        if (dto.getStudentId() != null) {
            studentRepository.findById(dto.getStudentId()).ifPresent(g::setStudent);
        }
        if (dto.getSubjectId() != null) {
            subjectRepository.findById(dto.getSubjectId()).ifPresent(g::setSubject);
        }
        if (dto.getTeacherId() != null) {
            teacherRepository.findById(dto.getTeacherId()).ifPresent(g::setTeacher);
        }

        return g;
    }

    private ExamDTO toExamDTO(Exam e) {
        if (e == null) return null;
        ExamDTO.ExamDTOBuilder builder = ExamDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .examDate(e.getExamDate())
                .examType(e.getExamType())
                .coefficient(e.getCoefficient())
                .room(e.getRoom())
                .durationMinutes(e.getDurationMinutes())
                .instructions(e.getInstructions());

        if (e.getSubject() != null) {
            builder.subjectId(e.getSubject().getId());
            builder.subjectName(e.getSubject().getName());
        }

        return builder.build();
    }
}
