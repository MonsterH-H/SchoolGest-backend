package com.example.schoolgestapp.gestions_users.controller;

import com.example.schoolgestapp.gestions_users.dto.TeacherDashboardDTO;
import java.util.List;
import com.example.schoolgestapp.gestions_users.services.TeacherDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@Tag(name = "03. Enseignant", description = "Endpoints pour l'espace enseignant")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherDashboardService dashboardService;

    @GetMapping("/{id}/dashboard-stats")
    @Operation(summary = "Statistiques du tableau de bord enseignant")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<TeacherDashboardDTO> getDashboardStats(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getTeacherStats(id));
    }

    @GetMapping("/{id}/assignments-pending")
    @Operation(summary = "Liste des devoirs en attente de correction")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<List<?>> getPendingAssignments(@PathVariable Long id) {
        // Pour l'instant on retourne une liste vide ou on pourrait implémenter la logique
        return ResponseEntity.ok(List.of());
    }
}
