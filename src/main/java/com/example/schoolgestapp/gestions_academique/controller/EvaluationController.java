package com.example.schoolgestapp.gestions_academique.controller;

import com.example.schoolgestapp.entity.Assignment;
import com.example.schoolgestapp.entity.Exam;
import com.example.schoolgestapp.entity.enums.EvaluationType;
import com.example.schoolgestapp.gestions_academique.dto.ExamDTO;
import com.example.schoolgestapp.gestions_academique.dto.GradeDTO;
import com.example.schoolgestapp.gestions_academique.services.EvaluationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluations")
@Tag(name = "10. Évaluations & notes", description = "Gestion des notes de CC, examens finaux, calcul de moyennes et workflow de validation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // --- Devoirs & Examens ---
    @PostMapping("/assignments")
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return ResponseEntity.ok(evaluationService.createAssignment(assignment));
    }

    @PostMapping("/exams")
    public ResponseEntity<ExamDTO> createExam(@RequestBody Exam exam) {
        return ResponseEntity.ok(evaluationService.createExam(exam));
    }

    // --- Saisie de notes ---
    @PostMapping("/grades")
    public ResponseEntity<GradeDTO> submitGrade(@RequestBody GradeDTO grade) {
        return ResponseEntity.ok(evaluationService.saveGrade(grade));
    }

    @PostMapping("/grades/batch")
    public ResponseEntity<List<GradeDTO>> submitGradesBatch(@RequestBody List<GradeDTO> grades) {
        return ResponseEntity.ok(evaluationService.saveGradesInBatch(grades));
    }

    // --- Workflow ---
    @PatchMapping("/publish")
    public ResponseEntity<Void> publish(@RequestParam Long subjectId, @RequestParam EvaluationType type) {
        evaluationService.publishGradesForSubject(subjectId, type);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/validate")
    public ResponseEntity<Void> validate(@RequestParam Long subjectId) {
        evaluationService.validateGradesForSubject(subjectId);
        return ResponseEntity.ok().build();
    }

    // --- Résultats & Statistiques ---
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Double> getAverage(@PathVariable Long studentId, @RequestParam Long subjectId) {
        return ResponseEntity.ok(evaluationService.calculateSubjectAverage(studentId, subjectId));
    }

    @GetMapping("/subject/{subjectId}/stats")
    public ResponseEntity<Map<String, Double>> getStats(@PathVariable Long subjectId) {
        return ResponseEntity.ok(evaluationService.getSubjectStats(subjectId));
    }

    /**
     * Consulter toutes ses notes (etudiant).
     */
    @GetMapping("/etudiant/{studentId}/toutes")
    public ResponseEntity<List<GradeDTO>> getGrades(@PathVariable Long studentId) {
        return ResponseEntity.ok(evaluationService.getGradesByStudent(studentId));
    }
}
