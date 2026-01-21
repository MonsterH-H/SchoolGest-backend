package com.example.schoolgestapp.gestions_academique.controller;

import com.example.schoolgestapp.entity.*;
import com.example.schoolgestapp.gestions_academique.dto.*;
import com.example.schoolgestapp.gestions_academique.services.AcademicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/structure")
@Tag(name = "04. Structure académique", description = "Gestion d'établissements, classes, matières, semestres et inscriptions")
public class AcademicController {

    private final AcademicService academicService;

    public AcademicController(AcademicService academicService) {
        this.academicService = academicService;
    }

    // --- Etablissements ---
    @PostMapping("/establishments")
    public ResponseEntity<EstablishmentDTO> createEstablishment(@RequestBody Establishment e) {
        return ResponseEntity.ok(academicService.saveEstablishment(e));
    }

    @GetMapping("/establishments")
    public ResponseEntity<List<EstablishmentDTO>> getEstablishments() {
        return ResponseEntity.ok(academicService.getAllEstablishments());
    }

    @PutMapping("/establishments/{id}")
    public ResponseEntity<EstablishmentDTO> updateEstablishment(@PathVariable Long id, @RequestBody Establishment e) {
        return ResponseEntity.ok(academicService.updateEstablishment(id, e));
    }

    @DeleteMapping("/establishments/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        academicService.deleteEstablishment(id);
        return ResponseEntity.noContent().build();
    }

    // --- Modules ---
    @PostMapping("/modules")
    public ResponseEntity<ModuleDTO> createModule(@RequestBody com.example.schoolgestapp.entity.Module m) {
        return ResponseEntity.ok(academicService.saveModule(m));
    }

    @GetMapping("/modules")
    public ResponseEntity<List<ModuleDTO>> getModules() {
        return ResponseEntity.ok(academicService.getAllModules());
    }

    @GetMapping("/classes/{id}/modules")
    public ResponseEntity<List<ModuleDTO>> getModulesByClasse(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getModulesByClasse(id));
    }

    @PutMapping("/modules/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        return ResponseEntity.ok(academicService.updateModule(id, dto));
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        academicService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    // --- Matières (Subjects) ---
    @PostMapping("/subjects")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody Subject s) {
        return ResponseEntity.ok(academicService.saveSubject(s));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjects() {
        return ResponseEntity.ok(academicService.getAllSubjects());
    }

    @GetMapping("/classes/{id}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByClasse(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getSubjectsByClasse(id));
    }

    @GetMapping("/modules/{id}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByModule(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getSubjectsByModule(id));
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(academicService.updateSubject(id, dto));
    }

    @PutMapping("/subjects/{id}/module")
    public ResponseEntity<SubjectDTO> assignSubjectToModule(@PathVariable Long id, @RequestParam Long moduleId) {
        return ResponseEntity.ok(academicService.assignSubjectToModule(id, moduleId));
    }

    @DeleteMapping("/subjects/{id}/module")
    public ResponseEntity<SubjectDTO> unassignSubjectFromModule(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.unassignSubjectFromModule(id));
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        academicService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    // --- Classes & Groupes ---
    @PostMapping("/classes")
    public ResponseEntity<ClasseDTO> createClasse(@RequestBody Classe c) {
        return ResponseEntity.ok(academicService.saveClasse(c));
    }

    @GetMapping("/classes")
    public ResponseEntity<List<ClasseDTO>> getClasses() {
        return ResponseEntity.ok(academicService.getAllClasses());
    }

    @PutMapping("/classes/{id}")
    public ResponseEntity<ClasseDTO> updateClasse(@PathVariable Long id, @RequestBody Classe c) {
        return ResponseEntity.ok(academicService.updateClasse(id, c));
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        academicService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classes/{id}/students")
    public ResponseEntity<List<com.example.schoolgestapp.gestions_users.dto.StudentDTO>> getStudentsByClasse(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getStudentsByClasse(id));
    }

    @PostMapping("/classes/{id}/groups")
    public ResponseEntity<ClasseDTO> createGroup(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(academicService.createGroup(id, body.get("name"), body.get("type")));
    }

    // --- Semestres ---
    @PostMapping("/semesters")
    public ResponseEntity<Semester> createSemester(@RequestBody Semester s) {
        return ResponseEntity.ok(academicService.saveSemester(s));
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<Semester>> getSemesters() {
        return ResponseEntity.ok(academicService.getAllSemesters());
    }

    @PutMapping("/semesters/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable Long id, @RequestBody Semester s) {
        return ResponseEntity.ok(academicService.updateSemester(id, s));
    }

    @DeleteMapping("/semesters/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        academicService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }

    // --- Inscription ---
    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enroll(@RequestParam Long studentId, @RequestParam Long classeId) {
        return ResponseEntity.ok(academicService.enrollStudent(studentId, classeId));
    }

    @DeleteMapping("/enroll")
    public ResponseEntity<Void> unenroll(@RequestParam Long studentId, @RequestParam Long classeId) {
        academicService.unenrollStudent(studentId, classeId);
        return ResponseEntity.noContent().build();
    }
}
