package com.example.schoolgestapp.gestions_presences.controller;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.gestions_presences.dto.AttendanceDTO;
import com.example.schoolgestapp.gestions_presences.dto.AttendanceRequestDTO;
import com.example.schoolgestapp.gestions_presences.dto.BatchAttendanceRequestDTO;
import com.example.schoolgestapp.gestions_presences.services.AttendanceService;
import com.example.schoolgestapp.gestions_ressources.services.FileUploadService;
import com.example.schoolgestapp.repository.IUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * CONTROLEUR : Gestion des Présences avec upload des justificatifs.
 */
@RestController
@RequestMapping("/api/presences")
@Tag(name = "09. Présences & Assiduité", description = "Appel en classe, gestion des absences et validation des justificatifs médicaux/administratifs")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final FileUploadService fileUploadService;
    private final IUser userRepository;

    public AttendanceController(AttendanceService attendanceService, 
                                FileUploadService fileUploadService, 
                                IUser userRepository) {
        this.attendanceService = attendanceService;
        this.fileUploadService = fileUploadService;
        this.userRepository = userRepository;
    }

    @PostMapping("/marquer")
    public ResponseEntity<AttendanceDTO> mark(@RequestBody AttendanceRequestDTO request) {
        return ResponseEntity.ok(attendanceService.markAttendance(
                request.getStudentId(), 
                request.getPlanningId(), 
                request.getStatus(), 
                request.getNotes()));
    }

    @PostMapping("/marquer/batch")
    public ResponseEntity<List<AttendanceDTO>> markBatch(@RequestBody BatchAttendanceRequestDTO request) {
        return ResponseEntity.ok(attendanceService.markAttendanceBatch(request));
    }

    // --- Justifier une absence avec un fichier physique ---
    @PostMapping(value = "/{id}/justifier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttendanceDTO> justifyWithFile(
            @PathVariable Long id,
            @RequestParam("reason") String reason,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = fileUploadService.uploadFile(file, "justificatifs");
        }
        
        return ResponseEntity.ok(attendanceService.submitJustification(id, reason, fileUrl));
    }

    @PatchMapping("/{id}/valider-justificatif")
    public ResponseEntity<AttendanceDTO> validate(@PathVariable Long id, 
                                               @RequestParam boolean accepted,
                                               @AuthenticationPrincipal String username) {
        User validator = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(attendanceService.validateJustification(id, accepted, validator));
    }

    @GetMapping("/stats/etudiant/{id}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getStudentStats(id));
    }

    /** Lister son historique d'assiduité (etudiant). */
    @GetMapping("/etudiant/{id}")
    public ResponseEntity<List<AttendanceDTO>> getByStudent(@PathVariable Long id) {
        // Fix potential conflict: Ensure this method maps correctly and doesn't conflict with other paths.
        // Also check if id is valid.
        return ResponseEntity.ok(attendanceService.getAttendancesByStudent(id));
    }
}
