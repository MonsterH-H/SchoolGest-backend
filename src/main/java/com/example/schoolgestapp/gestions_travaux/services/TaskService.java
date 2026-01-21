package com.example.schoolgestapp.gestions_travaux.services;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.exception.BusinessException;
import com.example.schoolgestapp.gestions_travaux.dto.*;
import com.example.schoolgestapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE : Gestion des Travaux Pédagogiques (Devoirs et Rendus)
 * 
 * Ce service permet aux enseignants de publier des devoirs et aux etudiants
 * de soumettre leurs travaux. Il gère également la détection des retards
 * et la notation avec feedback.
 */
@Service
@Transactional
public class TaskService {

    private final IAssignment assignmentRepository;
    private final ISubmission submissionRepository;
    private final IStudent studentRepository;
    private final IClasse classeRepository;
    private final ISubject subjectRepository;
    private final ITeacher teacherRepository;

    public TaskService(IAssignment assignmentRepository, 
                       ISubmission submissionRepository, 
                       IStudent studentRepository,
                       IClasse classeRepository,
                       ISubject subjectRepository,
                       ITeacher teacherRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.studentRepository = studentRepository;
        this.classeRepository = classeRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
    }

    /* ===================== COTE ENSEIGNANT ===================== */

    /**
     * Publie un nouveau devoir pour une classe spécifique.
     */
    public AssignmentResponseDTO createAssignment(AssignmentCreateDTO dto) {
        Assignment a = new Assignment();
        a.setTitle(dto.getTitle());
        a.setDescription(dto.getDescription());
        a.setDeadline(dto.getDeadline());
        a.setAttachedFileUrl(dto.getAttachedFileUrl());
        a.setClasse(classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new BusinessException("Classe non trouvée")));
        a.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new BusinessException("Matière non trouvée")));
        a.setTeacher(teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new BusinessException("Enseignant non trouvé")));
        
        return toAssignmentDTO(assignmentRepository.save(a));
    }

    public AssignmentResponseDTO updateAssignment(Long id, Assignment a) {
        // Simple update for now
        a.setId(id);
        return toAssignmentDTO(assignmentRepository.save(a));
    }
    
    public Assignment getRawAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Devoir introuvable"));
    }

    public AssignmentResponseDTO getAssignmentById(Long id) {
        return toAssignmentDTO(assignmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Devoir introuvable")));
    }

    /**
     * Récupère la liste de tous les travaux rendus par les etudiants pour un devoir donné.
     * @param assignmentId ID du devoir concerné.
     */
    public List<SubmissionResponseDTO> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignment(assignmentRepository.getReferenceById(assignmentId))
                .stream()
                .map(this::toSubmissionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Attribue une note et un commentaire à un travail rendu.
     * @param submissionId ID du rendu à corriger.
     * @param grade Note sur 20.
     * @param feedback Commentaire pédagogique.
     */
    public SubmissionResponseDTO gradeSubmission(Long submissionId, Double grade, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("Travail rendu introuvable"));
        
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return toSubmissionDTO(submissionRepository.save(submission));
    }

    /* ===================== COTE ETUDIANT ===================== */

    /**
     * Liste tous les devoirs assignés à la classe d'un etudiant.
     * @param studentId ID de l'etudiant.
     */
    public List<AssignmentResponseDTO> getAssignmentsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));
        return assignmentRepository.findByClasse(student.getClasse())
                .stream()
                .map(this::toAssignmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Soumet un travail pour un devoir.
     * Gère la mise à jour si un rendu existe déjà et vérifie automatiquement le retard.
     */
    public SubmissionResponseDTO submitWork(SubmissionCreateDTO dto) {
        Assignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new BusinessException("Devoir introuvable"));
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new BusinessException("etudiant introuvable"));

        // On cherche si l'etudiant a déjà rendu quelque chose pour ce devoir
        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new Submission());
        
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedFileUrl(dto.getSubmittedFileUrl());
        submission.setSubmissionText(dto.getSubmissionText());
        submission.setSubmissionDate(LocalDateTime.now());
        
        // Méthode interne de l'entité qui compare avec la deadline du devoir
        submission.checkIfLate();

        return toSubmissionDTO(submissionRepository.save(submission));
    }

    /* ===================== mapper ===================== */

    private AssignmentResponseDTO toAssignmentDTO(Assignment a) {
        return AssignmentResponseDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .deadline(a.getDeadline())
                .attachedFileUrl(a.getAttachedFileUrl())
                .solutionFileUrl(a.getSolutionFileUrl())
                .classeId(a.getClasse() != null ? a.getClasse().getId() : null)
                .classeName(a.getClasse() != null ? a.getClasse().getName() : null)
                .subjectId(a.getSubject().getId())
                .subjectName(a.getSubject().getName())
                .teacherId(a.getTeacher().getId())
                .teacherName(a.getTeacher().getUser().getFirstName() + " " + a.getTeacher().getUser().getLastName())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private SubmissionResponseDTO toSubmissionDTO(Submission s) {
        return SubmissionResponseDTO.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment().getId())
                .assignmentTitle(s.getAssignment().getTitle())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getUser().getFirstName() + " " + s.getStudent().getUser().getLastName())
                .submissionDate(s.getSubmissionDate())
                .submittedFileUrl(s.getSubmittedFileUrl())
                .submissionText(s.getSubmissionText())
                .late(s.isLate())
                .grade(s.getGrade())
                .feedback(s.getFeedback())
                .build();
    }
}
