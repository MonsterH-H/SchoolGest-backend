package com.example.schoolgestapp.gestions_travaux.controller;

import com.example.schoolgestapp.entity.Assignment;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.example.schoolgestapp.gestions_travaux.dto.*;
import com.example.schoolgestapp.gestions_travaux.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * CONTROLEUR : Gestion des Travaux avec support d'upload Cloud.
 */
@RestController
@RequestMapping("/api/travaux")
@Tag(name = "05. Travaux & Devoirs", description = "Distribution des devoirs, collecte des rendus et notation interactive")
public class TaskController {

    private final TaskService taskService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public TaskController(TaskService taskService, FileUploadService fileUploadService, ObjectMapper objectMapper) {
        this.taskService = taskService;
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
    }

    // --- Enseignant : Création de devoir avec fichier ---
    @PostMapping(value = "/devoirs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentResponseDTO> createWithFile(
            @RequestPart("assignment") String assignmentJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        
        AssignmentCreateDTO dto = objectMapper.readValue(assignmentJson, AssignmentCreateDTO.class);
        
        if (file != null && !file.isEmpty()) {
            String url = fileUploadService.uploadFile(file, "devoirs");
            dto.setAttachedFileUrl(url);
        }
        
        return ResponseEntity.ok(taskService.createAssignment(dto));
    }

    @GetMapping("/devoirs/{id}/soumissions")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissions(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getSubmissionsByAssignment(id));
    }

    @PatchMapping("/soumissions/{id}/noter")
    public ResponseEntity<SubmissionResponseDTO> grade(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Double grade = Double.valueOf(body.get("grade").toString());
        String feedback = (String) body.get("feedback");
        return ResponseEntity.ok(taskService.gradeSubmission(id, grade, feedback));
    }

    /**
     * Enseignant : Ajouter un corrigé (solution) au devoir.
     */
    @PatchMapping(value = "/devoirs/{id}/solution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentResponseDTO> uploadSolution(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        
        String url = fileUploadService.uploadFile(file, "solutions");
        Assignment assignment = taskService.getRawAssignmentById(id);
        assignment.setSolutionFileUrl(url);
        return ResponseEntity.ok(taskService.updateAssignment(id, assignment));
    }

    // --- etudiant : Rendu de devoir avec fichier ---
    @PostMapping(value = "/devoirs/{id}/rendre", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponseDTO> submitWithFile(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam(value = "text", required = false) String text,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = fileUploadService.uploadFile(file, "soumissions");
        }
        
        SubmissionCreateDTO dto = new SubmissionCreateDTO();
        dto.setAssignmentId(id);
        dto.setStudentId(studentId);
        dto.setSubmissionText(text);
        dto.setSubmittedFileUrl(fileUrl);
        
        return ResponseEntity.ok(taskService.submitWork(dto));
    }

    @GetMapping("/etudiant/{id}/devoirs")
    public ResponseEntity<List<AssignmentResponseDTO>> getStudentAssignments(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getAssignmentsForStudent(id));
    }
}
