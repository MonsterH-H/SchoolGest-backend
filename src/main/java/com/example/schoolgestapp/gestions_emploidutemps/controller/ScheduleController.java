package com.example.schoolgestapp.gestions_emploidutemps.controller;

import com.example.schoolgestapp.entity.Salle;
import com.example.schoolgestapp.entity.TimeSlot;
import com.example.schoolgestapp.gestions_emploidutemps.dto.PlanningDTO;
import com.example.schoolgestapp.gestions_emploidutemps.dto.SalleDTO;
import com.example.schoolgestapp.gestions_emploidutemps.dto.TimeSlotDTO;
import com.example.schoolgestapp.gestions_emploidutemps.services.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emploidutemps")
@Tag(name = "12. Emploi du Temps", description = "Planification des cours, gestion des salles de classe, des créneaux horaires et reports de séances")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // --- Configuration des Salles ---
    @PostMapping("/salles")
    public ResponseEntity<SalleDTO> creerSalle(@RequestBody Salle salle) { 
        return ResponseEntity.ok(scheduleService.creerSalle(salle)); 
    }
    
    @GetMapping("/salles")
    public ResponseEntity<List<SalleDTO>> listerSalles() {
        return ResponseEntity.ok(scheduleService.listerToutesLesSalles());
    }

    // --- Configuration des Créneaux ---
    @PostMapping("/creneaux")
    public ResponseEntity<TimeSlotDTO> creerCreneau(@RequestBody TimeSlot slot) { 
        return ResponseEntity.ok(scheduleService.creerTimeSlot(slot)); 
    }

    @GetMapping("/creneaux")
    public ResponseEntity<List<TimeSlotDTO>> listerCreneaux() {
        return ResponseEntity.ok(scheduleService.listerTousLesTimeSlots());
    }

    @PutMapping("/creneaux/{id}")
    public ResponseEntity<TimeSlotDTO> updateCreneau(@PathVariable Long id, @RequestBody TimeSlot slot) {
        return ResponseEntity.ok(scheduleService.updateTimeSlot(id, slot));
    }

    @DeleteMapping("/creneaux/{id}")
    public ResponseEntity<Void> deleteCreneau(@PathVariable Long id) {
        scheduleService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }

    // --- Planification ---
    @PostMapping("/plannings")
    public ResponseEntity<PlanningDTO> planifierCours(@RequestBody PlanningDTO planning) {
        return ResponseEntity.ok(scheduleService.planifierCours(planning));
    }

    @PutMapping("/plannings/{id}")
    public ResponseEntity<PlanningDTO> updatePlanning(@PathVariable Long id, @RequestBody PlanningDTO planning) {
        return ResponseEntity.ok(scheduleService.updatePlanning(id, planning));
    }

    @DeleteMapping("/plannings/{id}")
    public ResponseEntity<Void> deletePlanning(@PathVariable Long id) {
        scheduleService.deletePlanning(id);
        return ResponseEntity.noContent().build();
    }

    // --- Consultation ---
    @GetMapping("/classe/{id}")
    public ResponseEntity<List<PlanningDTO>> getParClasse(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(scheduleService.getPlanningParClasse(id, debut, fin));
    }

    @GetMapping("/enseignant/{id}")
    public ResponseEntity<List<PlanningDTO>> getParEnseignant(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(scheduleService.getPlanningParEnseignant(id, debut, fin));
    }

    // --- Modifications ---
    @PatchMapping("/plannings/{id}/annuler")
    public ResponseEntity<PlanningDTO> annuler(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(scheduleService.annulerSeance(id, body.get("motif")));
    }

    @PostMapping("/plannings/{id}/reporter")
    public ResponseEntity<PlanningDTO> reporter(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LocalDate nouvelleDate = LocalDate.parse((String) body.get("date"));
        Long nouveauCreneauId = Long.valueOf(body.get("timeSlotId").toString());
        return ResponseEntity.ok(scheduleService.reporterSeance(id, nouvelleDate, nouveauCreneauId));
    }
}
