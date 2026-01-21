package com.example.schoolgestapp.gestions_admin.controller;

import com.example.schoolgestapp.gestions_admin.dto.DashboardStatsDTO;
import com.example.schoolgestapp.gestions_admin.services.AdminDashboardService;
import com.example.schoolgestapp.gestions_admin.services.ImportExportService;
import com.example.schoolgestapp.gestions_users.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

/**
 * CONTROLEUR : Administration, Statistiques et Import/Export.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "07. Administration et Monitoring", description = "Tableaux de bord, Statistiques globales et outils d'import/export de données")
public class AdministrationController {

    private final AdminDashboardService adminDashboardService;
    private final ImportExportService importExportService;
    private final UserService userService;

    public AdministrationController(AdminDashboardService adminDashboardService, 
                                    ImportExportService importExportService,
                                    UserService userService) {
        this.adminDashboardService = adminDashboardService;
        this.importExportService = importExportService;
        this.userService = userService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(adminDashboardService.getGlobalDashboardStats());
    }

    /**
     * Import massif d'utilisateurs via CSV.
     */
    @PostMapping(value = "/import/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Map<String, Object>>> importUsers(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(importExportService.importUsersFromCsv(file));
    }

    /**
     * Export de la liste d'utilisateurs en CSV.
     */
    @GetMapping(value = "/export/users", produces = "text/csv")
    public ResponseEntity<String> exportUsers() {
        return ResponseEntity.ok(importExportService.exportUsersToCsv(userService.findAll()));
    }

    @GetMapping("/system/status")
    public ResponseEntity<DashboardStatsDTO> getStatus() {
        return ResponseEntity.ok(adminDashboardService.getGlobalDashboardStats());
    }
}
